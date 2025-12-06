import React, { useEffect, useState, useMemo } from "react";
import { CardLabel, Dropdown, LabelFieldPair, TextInput, Loader } from "@upyog/digit-ui-react-components";

const SelectVehicleType = ({ t, config, onSelect, formData, setValue }) => {
  const stateId = Digit.ULBService.getStateId();
  const tenantId = Digit.ULBService.getCurrentTenantId();

  // Fetch Vehicle Data
  const { data: vehicleData, isLoading: vehicleLoading } = Digit.Hooks.fsm.useMDMS(stateId, "Vehicle", "VehicleMakeModel");

  // Fetch Service Type Data
  const { data: serviceTypeData, isLoading: serviceLoading } = Digit.Hooks.useCustomMDMS(tenantId, "tenant", [{ name: "citymodule" }], {
    select: (data) => data?.tenant?.citymodule,
  });

  const [serviceTypeFilters, setServiceTypeFilters] = useState({});
  const [modals, setModals] = useState([]);
  const [selectedModal, setSelectedModal] = useState({});
  const [types, setTypes] = useState([]);
  const [selectedType, setSelectedType] = useState({});
  const [selectedCapacity, setSelectedCapacity] = useState("");

  // Get selected service type from formData
  const selectedServiceType = formData?.additionalDetails?.code;

  // Memoize the service type filters
  const memoizedServiceTypeFilters = useMemo(() => {
    if (!serviceTypeData || !vehicleData) return {};
    
    const filters = {};
    serviceTypeData.forEach(service => {
      if (service.code === "WT") {
        filters[service.code] = ["TANKER"];
      }
      else if (service.code === "FSM" || service.code === "CND") {
        filters[service.code] = vehicleData
          .filter(vehicle => ["MAHINDRA", "TATA", "TRACTOR", "TAFE", "SONALIKA"].includes(vehicle.code))
          .map(vehicle => vehicle.code);
      }
    });
    return filters;
  }, [serviceTypeData, vehicleData]);

  // Update service type filters
  useEffect(() => {
    setServiceTypeFilters(memoizedServiceTypeFilters);
  }, [memoizedServiceTypeFilters]);

  // Update the Model dropdown based on selected Service Type
  useEffect(() => {
    if (!vehicleData) return;
    
    const allowedModels = serviceTypeFilters[selectedServiceType] || [];
    const filteredModals = vehicleData.filter(vehicle => allowedModels.includes(vehicle.code));
    
    setModals(filteredModals);
    setSelectedModal({});
    setTypes([]);
    setSelectedType({});
    setSelectedCapacity("");
  }, [vehicleData, selectedServiceType, serviceTypeFilters]);

  // Update the Vehicle Type dropdown based on selected Model
  useEffect(() => {
    if (!vehicleData || !selectedModal?.code) return;
    
    const filteredTypes = vehicleData.filter(vehicle => vehicle.make === selectedModal.code);
    setTypes(filteredTypes);
    setSelectedType({});
    setSelectedCapacity("");
  }, [selectedModal, vehicleData]);

  // Set the Capacity when a Type is selected
  useEffect(() => {
    if (selectedType?.capacity) {
      setSelectedCapacity(selectedType.capacity);
    }
  }, [selectedType]);

  // Handler for selecting a Model
  const selectModal = (modal) => {
    setSelectedModal(modal);
    onSelect(config.key, { ...formData?.[config.key] || {}, modal: modal, type: "" });
  };

  // Handler for selecting a Vehicle Type
  const selectType = (type) => {
    setSelectedType(type);
    setSelectedCapacity(type.capacity);
    onSelect(config.key, { ...formData[config.key], type: type });
  };

  if (vehicleLoading || serviceLoading) {
    return <Loader />;
  }

  if (!vehicleData || !serviceTypeData) {
    return <div>{t("ERROR_FETCHING_DATA")}</div>;
  }

  return (
    <div>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">
          {t("ES_FSM_REGISTRY_VEHICLE_MODEL")}
        </CardLabel>
        <Dropdown
          className="form-field"
          selected={selectedModal}
          option={modals}
          select={selectModal}
          optionKey="name"
          t={t}
        />
      </LabelFieldPair>
{/* Vehicle Type Dropdown (Filtered based on Model) */}
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">
          {t("ES_FSM_REGISTRY_VEHICLE_TYPE")}
        </CardLabel>
        <Dropdown
          className="form-field"
          selected={selectedType}
          option={types}
          select={selectType}
          optionKey="name"
          t={t}
        />
      </LabelFieldPair>
{/* Vehicle Capacity Input (Set Automatically Based on Type) */}
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">
          {t("ES_FSM_REGISTRY_VEHICLE_CAPACITY")}
        </CardLabel>
        <TextInput
          className=""
          textInputStyle={{ width: "50%" }}
          value={selectedCapacity}
          disable={true}
        />
      </LabelFieldPair>
    </div>
  );
};

export default SelectVehicleType;