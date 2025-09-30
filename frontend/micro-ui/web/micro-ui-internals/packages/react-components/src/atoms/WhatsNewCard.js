import React from "react";
import { useTranslation } from "react-i18next";

const WhatsNewCard = ({
  header,
  actions,
  eventNotificationText,
  timePastAfterEventCreation,
  timeApproxiamationInUnits,
  eventDate,
  auditDetails
 // pass event date if available
}) => {
  const { t } = useTranslation();
  const ConvertEpochToDate = (dateEpoch) => {
    if (dateEpoch == null || dateEpoch === "") {
      return "NA";
    }
  
    const dateFromApi = new Date(dateEpoch);
    const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    
    const monthName = months[dateFromApi.getMonth()];
    const day = dateFromApi.getDate();
    const year = dateFromApi.getFullYear();
  
    return `${monthName} ${day}, ${year}`;
  };
  
  const getTransformedLocale = (label) => {
    if (typeof label === "number") return label;
    return label && label.toUpperCase().replace(/[.:-\s\/]/g, "_");
  };
console.log("dddddddddddd", auditDetails)
  return (
    <div className="card news-card shadow-sm border-0 mb-3" style={{padding:"0px",margin:"0px"}}>
        <style>
            {
                `
                .card {
                    --#{$prefix}card-spacer-y: #{$card-spacer-y};
                    --#{$prefix}card-spacer-x: #{$card-spacer-x};
                    --#{$prefix}card-title-spacer-y: #{$card-title-spacer-y};
                    --#{$prefix}card-title-color: #{$card-title-color};
                    --#{$prefix}card-subtitle-color: #{$card-subtitle-color};
                    --#{$prefix}card-border-width: #{$card-border-width};
                    --#{$prefix}card-border-color: #{$card-border-color};
                    --#{$prefix}card-border-radius: #{$card-border-radius};
                    --#{$prefix}card-box-shadow: #{$card-box-shadow};
                    --#{$prefix}card-inner-border-radius: #{$card-inner-border-radius};
                    --#{$prefix}card-cap-padding-y: #{$card-cap-padding-y};
                    --#{$prefix}card-cap-padding-x: #{$card-cap-padding-x};
                    --#{$prefix}card-cap-bg: #{$card-cap-bg};
                    --#{$prefix}card-cap-color: #{$card-cap-color};
                    --#{$prefix}card-height: #{$card-height};
                    --#{$prefix}card-color: #{$card-color};
                    --#{$prefix}card-bg: #{$card-bg};
                    --#{$prefix}card-img-overlay-padding: #{$card-img-overlay-padding};
                    --#{$prefix}card-group-margin: #{$card-group-margin};
      
                  
                    position: relative;
                    display: flex;
                    flex-direction: column;
                    min-width: 0;
                    height: var(--#{$prefix}card-height);
                    color: var(--#{$prefix}body-color);
                    word-wrap: break-word;
                    background-color: var(--#{$prefix}card-bg);
                    background-clip: border-box;
                    border: var(--#{$prefix}card-border-width) solid var(--#{$prefix}card-border-color);
                    @include border-radius(var(--#{$prefix}card-border-radius));
                    @include box-shadow(var(--#{$prefix}card-box-shadow));
                  
                    > hr {
                      margin-right: 0;
                      margin-left: 0;
                    }
                  
                    > .list-group {
                      border-top: inherit;
                      border-bottom: inherit;
                  
                      &:first-child {
                        border-top-width: 0;
                        @include border-top-radius(var(--#{$prefix}card-inner-border-radius));
                      }
                  
                      &:last-child  {
                        border-bottom-width: 0;
                        @include border-bottom-radius(var(--#{$prefix}card-inner-border-radius));
                      }
                    }
                  
                    
                    > .card-header + .list-group,
                    > .list-group + .card-footer {
                      border-top: 0;
                    }
                  }
                  
                  .card-body {
                   
                    flex: 1 1 auto;
                    padding: var(--#{$prefix}card-spacer-y) var(--#{$prefix}card-spacer-x);
                    color: var(--#{$prefix}card-color);
                  }
                  
                  .card-title {
                    margin-bottom: var(--#{$prefix}card-title-spacer-y);
                    color: var(--#{$prefix}card-title-color);
                  }
                  
                  .card-subtitle {
                    margin-top: calc(-.5 * var(--#{$prefix}card-title-spacer-y)); 
                    margin-bottom: 0;
                    color: var(--#{$prefix}card-subtitle-color);
                  }
                  
                  .card-text:last-child {
                    margin-bottom: 0;
                  }
                  
                  .card-link {
                    &:hover {
                      text-decoration: if($link-hover-decoration == underline, none, null);
                    }
                  
                    + .card-link {
                      margin-left: var(--#{$prefix}card-spacer-x);
                    }
                  }
                  
              
                  
                  .card-header {
                    padding: var(--#{$prefix}card-cap-padding-y) var(--#{$prefix}card-cap-padding-x);
                    margin-bottom: 0; 
                    color: var(--#{$prefix}card-cap-color);
                    background-color: var(--#{$prefix}card-cap-bg);
                    border-bottom: var(--#{$prefix}card-border-width) solid var(--#{$prefix}card-border-color);
                  
                    &:first-child {
                      @include border-radius(var(--#{$prefix}card-inner-border-radius) var(--#{$prefix}card-inner-border-radius) 0 0);
                    }
                  }
                  
                  .card-footer {
                    padding: var(--#{$prefix}card-cap-padding-y) var(--#{$prefix}card-cap-padding-x);
                    color: var(--#{$prefix}card-cap-color);
                    background-color: var(--#{$prefix}card-cap-bg);
                    border-top: var(--#{$prefix}card-border-width) solid var(--#{$prefix}card-border-color);
                  
                    &:last-child {
                      @include border-radius(0 0 var(--#{$prefix}card-inner-border-radius) var(--#{$prefix}card-inner-border-radius));
                    }
                  }
                  
                  

                  
                  .card-header-tabs {
                    margin-right: calc(-.5 * var(--#{$prefix}card-cap-padding-x)); 
                    margin-bottom: calc(-1 * var(--#{$prefix}card-cap-padding-y)); 
                    margin-left: calc(-.5 * var(--#{$prefix}card-cap-padding-x)); 
                    border-bottom: 0;
                  
                    .nav-link.active {
                      background-color: var(--#{$prefix}card-bg);
                      border-bottom-color: var(--#{$prefix}card-bg);
                    }
                  }
                  
                  .card-header-pills {
                    margin-right: calc(-.5 * var(--#{$prefix}card-cap-padding-x));  
                    margin-left: calc(-.5 * var(--#{$prefix}card-cap-padding-x)); 
                  }
                  

                  .card-img-overlay {
                    position: absolute;
                    top: 0;
                    right: 0;
                    bottom: 0;
                    left: 0;
                    padding: var(--#{$prefix}card-img-overlay-padding);
                    @include border-radius(var(--#{$prefix}card-inner-border-radius));
                  }
                  
                  .card-img,
                  .card-img-top,
                  .card-img-bottom {
                    width: 100%; 
                  }
                  
                  .card-img,
                  .card-img-top {
                    @include border-top-radius(var(--#{$prefix}card-inner-border-radius));
                  }
                  
                  .card-img,
                  .card-img-bottom {
                    @include border-bottom-radius(var(--#{$prefix}card-inner-border-radius));
                  }
                  
                  

                  
                  .card-group {

                    > .card {
                      margin-bottom: var(--#{$prefix}card-group-margin);
                    }
                  
                    @include media-breakpoint-up(sm) {
                      display: flex;
                      flex-flow: row wrap;

                      > .card {
                        flex: 1 0 0%;
                        margin-bottom: 0;
                  
                        + .card {
                          margin-left: 0;
                          border-left: 0;
                        }
                  
                       
                        @if $enable-rounded {
                          &:not(:last-child) {
                            @include border-end-radius(0);
                  
                            .card-img-top,
                            .card-header {
                              
                              border-top-right-radius: 0;
                            }
                            .card-img-bottom,
                            .card-footer {
                              
                              border-bottom-right-radius: 0;
                            }
                          }
                  
                          &:not(:first-child) {
                            @include border-start-radius(0);
                  
                            .card-img-top,
                            .card-header {
                             
                              border-top-left-radius: 0;
                            }
                            .card-img-bottom,
                            .card-footer {
                              
                              border-bottom-left-radius: 0;
                            }
                          }
                        }
                      }
                    }
                  }
                  
                `
            }
        </style>
      <div className="card-body">
        {/* Title with icon */}
        <h6 className="card-title">
          <i className="fas fa-newspaper text-primary me-2"></i>
          {t(header)}
        </h6>

        {/* Small muted date or relative time */}
        <small className="text-muted">
          {ConvertEpochToDate(auditDetails?.createdTime)
            }
        </small>

        {/* Description */}
        <p className="mb-0 mt-2">{eventNotificationText}</p>

        {/* Actions */}
        {actions?.length > 0 && (
          <div className="mt-2">
            {actions.map((i, idx) => (
              <a
                key={idx}
                href={i?.actionUrl}
                className="btn btn-sm btn-outline-primary me-2"
              >
                {t(`CS_COMMON_${getTransformedLocale(i?.code)}`)}
              </a>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default WhatsNewCard;
