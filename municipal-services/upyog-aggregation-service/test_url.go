package main

import (
	"fmt"
	"net/url"
)

func main() {
	q := url.Values{}
	q.Add("tenantId", "pb")
	statuses := []string{"INITIATED", "APPLIED", "CHALLAN_GENERATED", "CREATED", "open", "OPEN", "SCHEDULED", "REQUESTCREATED", "REQUESTPENDING"}
	for _, s := range statuses {
		q.Add("status", s)
	}
	fmt.Println(q.Encode())
}
