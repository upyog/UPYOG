export const importTypeOptions = [
    {code: '1', name: 'IMPORT_TYPE_1'},
    {code: '2', name: 'IMPORT_TYPE_2'}
];

export const traderNameOptions = [
    {code: '1', name: 'TRADER_1'},
    {code: '2', name: 'TRADER_2'}
];

export const gawalNameOptions = [
    {code: '1', name: 'GAWAL_1'},
    {code: '2', name: 'GAWAL_2'}
];

export const brokerNameOptions = [
    {code: '1', name: 'BROKER_1'},
    {code: '2', name: 'BROKER_2'}
];

export const shopkeeperNameOptions = [
    {code: '1', name: 'SHOPKEEPER_1'},
    {code: '2', name: 'SHOPKEEPER_2'}
];

export const dawanwalaNameOptions = [
    {code: '1', name: 'DAWANWALA_1'},
    {code: '2', name: 'DAWANWALA_2'}
];

export const dairywalaNameOptions = [
    {code: '1', name: 'DAIRYWALA_1'},
    {code: '2', name: 'DAIRYWALA_2'}
];

export const vehicleTypeOptions = [
    {code: '1', name: 'VEHICLE_1'},
    {code: '2', name: 'VEHICLE_2'}
];

export const parkingMockData = {
    vehicleType: {code: '1', name: 'VEHICLE_1'},
    vehicleNumber: 'abc123',
    parkingDate: new Date().toISOString().split('T')[0],
    parkingTime: new Date().toTimeString().split(' ')[0],
    parkingAmount: 100
};

const tenDaysAgo = new Date();
tenDaysAgo.setDate(tenDaysAgo.getDate() - 10);

export const arrivalMockData = {
    importType: { code: '1', name: 'IMPORT_TYPE_1' },
    importPermissionNumber: "123456",
    importPermissionDate: tenDaysAgo.toISOString().split('T')[0],
    traderName: traderNameOptions[0],
    licenseNumber: "LIC123",
    vehicleNumber: "ABC123",
    numberOfAliveAnimals: 2,
    numberOfDeadAnimals: 1,
    arrivalDate: new Date().toISOString().split('T')[0],
    arrivalTime: new Date().toTimeString().split(' ')[0],
    gawalName: gawalNameOptions[0]
};

export const salsetteRemovalMockData = {
    traderName: { code: '1', name: 'TRADER_1' },
    brokerName: { code: '1', name: 'BROKER_1' },
    gawalName: { code: '1', name: 'GAWAL_1'},
    dairywalaName: { code: '1', name: 'DAIRYWALA_1'},
    numberOfAnimals: 1,
    animalTokenNumber: 1,
    removalDate: new Date().toISOString().split('T')[0],
    removalTime: new Date().toTimeString().split(' ')[0]
};

export const religiousPersonalMockData = {
    traderName: { code: '1', name: 'TRADER_1' },
    brokerName: { code: '1', name: 'BROKER_1' },
    gawalName: { code: '1', name: 'GAWAL_1'},
    citizenName: 'John Doe',
    numberOfAnimals: 1,
    animalTokenNumber: 1,
    removalDate: new Date().toISOString().split('T')[0],
    removalTime: new Date().toTimeString().split(' ')[0]
};

export const notSoldMockData = {
    traderName: { code: '1', name: 'TRADER_1' },
    brokerName: { code: '1', name: 'BROKER_1' },
    gawalName: { code: '1', name: 'GAWAL_1'},
    numberOfAnimals: 1,
    animalTokenNumber: 1,
    removalDate: new Date().toISOString().split('T')[0],
    removalTime: new Date().toTimeString().split(' ')[0]
};

export const rejectionBeforeTradingMockData = {
    traderName: { code: '1', name: 'TRADER_1' },
    brokerName: { code: '1', name: 'BROKER_1' },
    gawalName: { code: '1', name: 'GAWAL_1'},
    numberOfAnimals: 1,
    animalTokenNumber: 1,
    removalDate: new Date().toISOString().split('T')[0],
    removalTime: new Date().toTimeString().split(' ')[0]
};

export const rejectionAfterTradingMockData = {
    shopkeeperName: shopkeeperNameOptions[0],
    dawanwalaName: dawanwalaNameOptions[0],
    numberOfAnimals: 1,
    animalTokenNumber: 1,
    removalDate: new Date().toISOString().split('T')[0],
    removalTime: new Date().toTimeString().split(' ')[0]
};

export const deathBeforeTradingMockData = rejectionBeforeTradingMockData;

export const deathAfterTradingMockData = rejectionAfterTradingMockData;

export const paymentModeOptions = [
    {code: '1', name: 'PAYMENT_MODE_1'},
    {code: '2', name: 'PAYMENT_MODE_2'}
];

export const slaughterInAbbatoirMockData = {
    traderName: traderNameOptions[0],
    brokerName: brokerNameOptions[0],
    gawalName: gawalNameOptions[0],
    assignDate: new Date().toISOString().split('T')[0],
    shopkeeperName: shopkeeperNameOptions[0],
    dawanwalaName: dawanwalaNameOptions[0],
    shadeNumber: 1,
    numberOfAliveAnimals: 1,
    animalTokenNumber: 1,
    stablingDays: 2,
    stablingFeeAmount: 100,
    paymentMode: paymentModeOptions[0],
    paymentReferenceNumber: 1
};

export const religiousPersonalRemovalMockData = {
    traderName: traderNameOptions[0],
    brokerName: brokerNameOptions[0],
    gawalName: gawalNameOptions[0],
    assignDate: new Date().toISOString().split('T')[0],
    citizenName: 'John Doe',
    numberOfAnimals: 1,
    animalTokenNumber: 1,
    removalFeeAmount: 100,
    paymentMode: paymentModeOptions[0],
    paymentReferenceNumber: 100
};

export const salsetteRemovalShopkeeperAssignmentMockData = {
    traderName: traderNameOptions[0],
    brokerName: brokerNameOptions[0],
    gawalName: gawalNameOptions[0],
    assignDate: new Date().toISOString().split('T')[0],
    dairywalaName: dairywalaNameOptions[0],
    numberOfAnimals: 1,
    animalTokenNumber: 1,
    salsetteFeeAmount: 100,
    paymentMode: paymentModeOptions[0],
    paymentReferenceNumber: 1
};

export const entryFeeCollectionMockData = {
    importPermissionNumber: '123456',
    arrivalUuid: 'abc1234567890',
    traderName: traderNameOptions[0],
    licenseNumber: '123456abc',
    vehicleNumber: 'anc123',
    numberOfAliveAnimals: 1,
    arrivalDate: new Date().toISOString().split('T')[0],
    arrivalTime: new Date().toTimeString().split(' ')[0],
    gawalName: gawalNameOptions[0],
    brokerName: brokerNameOptions[0],
    entryFee: '100',
    receiptMode: paymentModeOptions[0],
    paymentReferenceNumber: '123456'
};