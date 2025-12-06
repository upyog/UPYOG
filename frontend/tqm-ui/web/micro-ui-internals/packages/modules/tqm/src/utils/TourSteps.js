export const Tour = {
  updateTest: [
    // {
    //   content:"Let me walk you through on how to upload test results",
    //   target:".app-container",
    //   disableBeacon:true,
    //   placement:"center",
    //   title:"This is your Home Screen"
    // },
    {
      content:
        'This is a list of tests that are pending to be updated.  Clicking on the Action Button will take you to the test update screen. Alternatively we can visit the inbox from the TQM home screen.',
      target: '.pending-tasks-container',
      disableBeacon: true,
      placement: 'top',
      title: 'How to Upload Test Results',
    },
    {
      content:
        'From the Landing screen, click on the Treatment Quality Card. Click on next to move further',
      target: '.tiles-card-2',
      disableBeacon: true,
      placement: 'auto',
      redirectTo: '/tqm-ui/employee/tqm/home',
      // title:"This is your Home Screen"
    },
    {
      content:
        'From the TQM Card on Home Screen, click on the Inbox link. Click on next to move further',
      target: '.complaint-links-container',
      disableBeacon: true,
      placement: 'auto',
      redirectTo: '/tqm-ui/employee/tqm/inbox',
      // title:"This is your Home Screen"
    },
    {
      content:
        'This is Inbox Screen. Here you will get the list of tests that are pending to be updated.You can search with applicable filters and sort the results using Filter and Sort Action respectively',
      target: '.searchBox',
      disableBeacon: true,
      placement: 'bottom',
      // redirectTo:"/tqm-ui/employee/tqm/search-test-results"
      // title:"This is your Home Screen"
    },
    {
      content: 'Click on the action button to update a test',
      target: '.ground-container',
      disableBeacon: true,
      placement: 'center',
      
      // redirectTo:"/tqm-ui/employee/tqm/search-test-results"
      // title:"This is your Home Screen"
    },
    {
      content: 'This is the update tests screen',
      target: '.ground-container',
      disableBeacon: true,
      placement: 'center',
      hideBackButton: true,
      stopTour:true,
      nextStepIndex:5
      // redirectTo:"/tqm-ui/employee/tqm/search-test-results"
      // title:"This is your Home Screen"
    },
    // {
    //   content:"Click on pending tasks to land on inbox",
    //   target:".action-link",
    //   disableBeacon:true,
    //   placement:"top",
    //   title:"How to Upload Test Results"
    // },
  ],
  viewTest: [
    {
      content: 'Let me walk you through on how to search tests',
      target: '.app-container',
      disableBeacon: true,
      placement: 'center',
      title: 'This is your Home Screen',
    },
    {
      content:
        'From the Landing screen, click on the Treatment Quality Card. Click on next to move further',
      target: '.tiles-card-2',
      disableBeacon: true,
      placement: 'auto',
      redirectTo: '/tqm-ui/employee/tqm/home',
      // title:"This is your Home Screen"
    },
    {
      content: 'This is your Home Screen for TQM',
      target: '.tqm-home-container',
      disableBeacon: true,
      placement: 'center',
      hideBackButton: true,
      // title:"This is your Home Screen"
    },
    {
      content:
        'From the TQM Card on Home Screen, click on the View Past Tests Link. Click on next to move further',
      target: '.complaint-links-container',
      disableBeacon: true,
      placement: 'auto',
      redirectTo: '/tqm-ui/employee/tqm/search-test-results',
      // title:"This is your Home Screen"
    },
    {
      content:
        'This is View Past Tests Screen. Here you will get the list of tests that are submitted, according to the filters that you have selected',
      target: '.ground-container',
      disableBeacon: true,
      placement: 'center',
      hideBackButton: true,
      // redirectTo:"/tqm-ui/employee/tqm/search-test-results"
      // title:"This is your Home Screen"
    },
    {
      content:
        'You can search with applicable filters and sort the results using Filter and Sort Action respectively',
      target: '.searchBox',
      disableBeacon: true,
      placement: 'bottom',
      // redirectTo:"/tqm-ui/employee/tqm/search-test-results"
      // title:"This is your Home Screen"
    },
    //Add more steps corresponding to search results when search api is working fine
  ],
  viewDashboard: [
    {
      content: 'Let me walk you through on how to view dashboard',
      target: '.app-container',
      disableBeacon: true,
      placement: 'center',
      title: 'How to view Dashboard',
    },
    {
      content:
        'From the Landing screen, click on the Treatment Quality Card. Click on next to move further',
      target: '.tiles-card-2',
      disableBeacon: true,
      placement: 'auto',
      redirectTo: '/tqm-ui/employee/tqm/home',
      // title:"This is your Home Screen"
    },
    {
      content: 'This is your Home Screen for TQM',
      target: '.tqm-home-container',
      disableBeacon: true,
      placement: 'center',
      hideBackButton: true,
      // title:"This is your Home Screen"
    },
    {
      content:
        'From the TQM Card on Home Screen, click on the Dashboard Link. Click on next to move further',
      target: '.complaint-links-container',
      disableBeacon: true,
      placement: 'auto',
      redirectTo: '/tqm-ui/employee/dss/dashboard/pqm',
      // title:"This is your Home Screen"
    },
  ],
  viewSensors: [],
};

export const TourSteps = {
  '/tqm-ui/employee/tqm/landing':[
    {
      content:
        'Landing Page',
      target: '.app-container',
      disableBeacon: true,
      placement: 'center',
      title:"This is your Home Screen"
    },
    {
      content:
        'Card 1',
      target: '.tiles-card-0',
      disableBeacon: true,
      placement: 'auto',
      title:"This is your Home Screen"
    },
    {
      content:
        'Card2',
      target: '.tiles-card-1',
      disableBeacon: true,
      placement: 'auto',
      title:"This is your Home Screen"
    },
    {
      content:
        'Card3',
      target: '.tiles-card-2',
      disableBeacon: true,
      placement: 'auto',
      title:"This is your Home Screen"
    },
    {
      content:
        'Card4',
      target: '.tiles-card-3',
      disableBeacon: true,
      placement: 'auto',
      title:"This is your Home Screen"
    },
    {
      content:
        'Card5',
      target: '.tiles-card-4',
      disableBeacon: true,
      placement: 'auto',
      title:"This is your Home Screen"
    },
    {
      content:
        'This is a list of tests that are pending to be updated.  Clicking on the Action Button will take you to the test update screen. Alternatively we can visit the inbox from the TQM home screen.',
      target: '.pending-tasks-container',
      disableBeacon: true,
      placement: 'top',
      title: 'View Pending Tests',
    },
  ]
}
