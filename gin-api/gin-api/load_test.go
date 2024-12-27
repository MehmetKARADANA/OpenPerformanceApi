package main

import (
    "fmt"
    "net/http"
    "sync"
    "time"
    "testing"
)

// LoadTestResult yapısı, yük testi sonuçlarını tutar
type LoadTestResult struct {
    TotalRequests      int
    SuccessfulCount    int
    ErrorCount         int
    AverageResponseTime float64
    MaxResponseTime    time.Duration
    MinResponseTime    time.Duration
}

// performLoadTest işlevi, yük testi gerçekleştirir ve sonuçları döndürür
func performLoadTest() LoadTestResult {
    const (
        numRequests = 1000 // Gönderilecek toplam istek sayısı
        concurrency = 100   // Aynı anda kaç istek gönderilecek
        token       = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3MWEyOTFiNzY0YThlMTEwNzlkMGYyZSIsImlhdCI6MTcyOTc2NzcwNywiZXhwIjoxNzMyMzU5NzA3fQ.ijMcqGw4OUOWG79jrXhmkAwGolVJUjNXp6Y15y-OY7Y" // Token
    )

    var wg sync.WaitGroup
    sem := make(chan struct{}, concurrency) // Eş zamanlılık kontrolü için kanal

    responseTimes := make([]time.Duration, 0, numRequests)
    var totalResponseTime time.Duration
    maxResponseTime := time.Duration(0)
    minResponseTime := time.Duration(1<<63 - 1) // Maksimum değeri başlat

    startTime := time.Now()

    for i := 0; i < numRequests; i++ {
        wg.Add(1)
        sem <- struct{}{} // Kanalın kapasitesini kontrol et

        go func(requestID int) {
            defer wg.Done()
            defer func() { <-sem }() // Kanalı serbest bırak

            // İstek gönderme işlemi
            req, err := http.NewRequest("GET", "http://localhost:8083/api/getRooms", nil)
            if err != nil {
                fmt.Printf("Request %d failed: %v\n", requestID, err)
                return
            }
            req.Header.Set("Authorization", "Bearer "+token)

            client := &http.Client{}
            start := time.Now() // Yanıt süresi ölçümü
            resp, err := client.Do(req)
            responseTime := time.Since(start) // Yanıt süresi hesaplama
            if err != nil {
                fmt.Printf("Request %d failed: %v\n", requestID, err)
                return
            }
            defer resp.Body.Close()

            // Yanıt kontrolü
            if resp.StatusCode == http.StatusOK {
                fmt.Printf("Request %d succeeded: Status Code %d\n", requestID, resp.StatusCode)
                totalResponseTime += responseTime
                responseTimes = append(responseTimes, responseTime)

                if responseTime > maxResponseTime {
                    maxResponseTime = responseTime
                }
                if responseTime < minResponseTime {
                    minResponseTime = responseTime
                }
            } else {
                fmt.Printf("Request %d failed: Status Code %d\n", requestID, resp.StatusCode)
            }
        }(i)
    }

    wg.Wait()
    elapsed := time.Since(startTime)

    // Sonuçları hesapla
    successfulCount := len(responseTimes)
    errorCount := numRequests - successfulCount
    averageResponseTime := 0.0
    if successfulCount > 0 {
        averageResponseTime = float64(totalResponseTime) / float64(successfulCount) / float64(time.Millisecond)
    }

    result := LoadTestResult{
        TotalRequests:      numRequests,
        SuccessfulCount:    successfulCount,
        ErrorCount:         errorCount,
        AverageResponseTime: averageResponseTime,
        MaxResponseTime:    maxResponseTime,
        MinResponseTime:    minResponseTime,
    }

    // Sonuçları yazdır
    fmt.Printf("Finished %d requests in %v\n", result.TotalRequests, elapsed)
    fmt.Printf("Total Requests: %d\n", result.TotalRequests)
    fmt.Printf("Successful Requests: %d\n", result.SuccessfulCount)
    fmt.Printf("Failed Requests: %d\n", result.ErrorCount)
    fmt.Printf("Average Response Time: %.2f ms\n", result.AverageResponseTime)
    fmt.Printf("Max Response Time: %v\n", result.MaxResponseTime)
    fmt.Printf("Min Response Time: %v\n", result.MinResponseTime)

    return result
}

// TestLoadTest işlevi, yük testi için bir test fonksiyonudur
func TestLoadTest(t *testing.T) {
    result := performLoadTest()

    // Test sonuçlarını kontrol et
    if result.TotalRequests != 1000 {
        t.Errorf("Expected total requests to be 1000, but got %d", result.TotalRequests)
    }
    if result.SuccessfulCount+result.ErrorCount != result.TotalRequests {
        t.Errorf("Successful and error counts do not add up to total requests.")
    }
}
