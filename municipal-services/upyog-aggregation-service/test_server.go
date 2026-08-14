package main

import (
	"fmt"
	"net/http"
)

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Printf("Received %s request to %s\n", r.Method, r.URL.Path)
		w.WriteHeader(http.StatusOK)
	})
	fmt.Println("Listening on :9999")
	http.ListenAndServe(":9999", nil)
}
