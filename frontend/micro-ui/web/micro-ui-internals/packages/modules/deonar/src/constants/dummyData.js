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
    traderName: { code: '1', name: 'TRADER_1' },
    licenseNumber: "LIC123",
    vehicleNumber: "ABC123",
    numberOfAliveAnimals: 2,
    numberOfDeadAnimals: 1,
    arrivalDate: new Date().toISOString().split('T')[0],
    arrivalTime: new Date().toTimeString().split(' ')[0],
};