package main

import (
    "context"
    "fmt"
    "net/http"
  //  "strings"
    "encoding/json"
    "io/ioutil"
    "log"
    "os"
    "sync"
    "time"
    "gopkg.in/yaml.v2"
    "github.com/gin-gonic/gin"
)

const (
    yamlFilePath = "servers.yaml"
)

var (
    ctx          = context.TODO()
    serverConfig ServerConfig
)

type Agency struct {
    Name       string `yaml:"name"`
    URL        string `yaml:"url"`
    DistanceKM int    `yaml:"distance_km"`
}

type ServerConfig struct {
    Agencies []Agency `yaml:"servers"`
}

var httpClient *http.Client

func init() {
    httpTransport := &http.Transport{
        MaxIdleConns:        20000, // Maximum number of idle connections
        MaxIdleConnsPerHost: 1000,  // Maximum connections per host
    }
    httpClient = &http.Client{
        Transport: httpTransport,
        Timeout:   10 * time.Second, // Timeout duration for requests
    }

    var err error
    file, err := os.Open(yamlFilePath)
    if err != nil {
        log.Fatalf("YAML file opening error: %v", err)
    }
    defer file.Close()

    decoder := yaml.NewDecoder(file)
    err = decoder.Decode(&serverConfig)
    if err != nil {
        log.Fatalf("YAML file reading error: %v", err)
    }
}

// Fetch JSON from an agency URL
func fetchJSON(url string) (interface{}, error) {
    resp, err := httpClient.Get(url)
    if err != nil {
        return nil, fmt.Errorf("HTTP request error: %v", err)
    }
    defer resp.Body.Close()

    body, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        return nil, fmt.Errorf("Response reading error: %v", err)
    }
    log.Printf("Response: %s\n", body)

    var result interface{}
    if err := json.Unmarshal(body, &result); err != nil {
        return nil, fmt.Errorf("JSON parse error: %v", err)
    }
    return result, nil
}

func getRooms(c *gin.Context) {
    var wg sync.WaitGroup
    results := make(chan map[string]interface{}, len(serverConfig.Agencies)) // Buffered channel to hold results
    workerLimit := make(chan struct{}, 50) // Limit the number of concurrent workers

    for _, agency := range serverConfig.Agencies {
        wg.Add(1)
        workerLimit <- struct{}{} // Acquire a slot in the worker pool
        go func(agency Agency) {
            defer wg.Done()
            defer func() { <-workerLimit }() // Release the slot in the worker pool when done

            jsonData, err := fetchJSON(agency.URL + "/rooms")
            if err != nil {
                log.Printf("Error with agency %s: %v", agency.Name, err)
                results <- map[string]interface{}{
                    "agency":      agency.Name,
                    "distance_km": agency.DistanceKM,
                    "error":       err.Error(),
                }
                return
            }

            results <- map[string]interface{}{
                "agency":      agency.Name,
                "distance_km": agency.DistanceKM,
                "data":        jsonData,
            }
        }(agency)
    }

    go func() {
        wg.Wait()
        close(results)
    }()

    var allResults []map[string]interface{}
    for result := range results {
        allResults = append(allResults, result)
    }

    c.JSON(http.StatusOK, allResults)
}

func main() {
    router := gin.Default()
    router.GET("/api/getRooms", getRooms)

    router.Run(":8083")
}
