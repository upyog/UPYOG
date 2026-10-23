package common

import (
	"context"
	"time"

	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// UserInfo represents the user information block within a RequestInfo.
type UserInfo struct {
	UUID string `json:"uuid"`
}

// RequestInfo is the standard wrapper required for making POST requests to eGov/UPYOG microservices.
type RequestInfo struct {
	APIID     string    `json:"apiId"`
	Ver       string    `json:"ver"`
	Ts        int64     `json:"ts"`
	MsgID     string   `json:"msgId"`
	AuthToken string   `json:"authToken"`
	UserInfo  UserInfo `json:"userInfo"`
}

// NewRequestInfo creates a standard RequestInfo structure populated from the context.
func NewRequestInfo(ctx context.Context, msgID string) RequestInfo {
	if msgID == "" {
		msgID = logger.RequestID(ctx)
	}

	ri := RequestInfo{
		APIID:     "upyog-aggregation-service",
		Ver:       "1.0",
		Ts:        time.Now().UnixMilli(),
		MsgID:     msgID,
		AuthToken: AuthToken(ctx),
	}

	ri.UserInfo = UserInfo{UUID: UserID(ctx)}

	return ri
}
