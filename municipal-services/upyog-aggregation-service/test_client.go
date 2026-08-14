package main

import (
	"context"
	"fmt"
	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"time"
)

func main() {
	log := logger.New("local")
	m := metrics.New("test", "test")
	c := clients.NewClient(clients.ClientConfig{
		ServiceName: "test",
		BaseURL: "http://localhost:9999",
		Timeout: 5 * time.Second,
	}, log, m)
	
	c.Post(context.Background(), "/test-path", nil, nil)
}
