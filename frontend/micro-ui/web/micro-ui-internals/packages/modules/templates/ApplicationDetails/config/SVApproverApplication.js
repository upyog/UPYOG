/**
 * custom component which will show the Small card so that user can fill their comment and take action
 * Returning the Header ad lable of the card as well as the comment box
 * 
 */

export const configSVApproverApplication = ({t,action}) => {
    return {
      label: {
        heading: `WF_${action?.action}_APPLICATION`,
        submit: `SV_${action?.action}`,
        cancel: "SV_COMMON_CANCEL",
      },
      form: [
        {
          body: [  
            {
              label: t("SV_ACTION_COMMENTS"),
              type: "textarea",
              populators: {
                name: "comments",
              },
            },
          ],
        },
      ],
    };
  };
  