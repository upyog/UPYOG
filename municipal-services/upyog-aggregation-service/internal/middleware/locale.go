package middleware

import (
	"github.com/gin-gonic/gin"

	"github.com/upyog/upyog-aggregation-service/internal/common"
)

// defaultLocale is the fallback locale when the Accept-Language header is
// absent or empty.
const defaultLocale = "en_IN"

// LocaleResolver returns a middleware that extracts the desired locale from
// the Accept-Language header. If the header is absent or empty, it defaults
// to "en_IN". The resolved locale is stored in the request context via
// common.WithLocale for downstream consumption.
func LocaleResolver() gin.HandlerFunc {
	return func(c *gin.Context) {
		locale := c.GetHeader(common.HeaderAcceptLanguage)
		if locale == "" {
			locale = defaultLocale
		}

		ctx := common.WithLocale(c.Request.Context(), locale)
		c.Request = c.Request.WithContext(ctx)

		c.Next()
	}
}
