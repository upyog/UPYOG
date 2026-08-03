import React from "react";

const EmployeeHome = ({ modules }) => {
  return (
    <div className="employee-app-container">
      <div className="ground-container moduleCardWrapper gridModuleWrapper">
        {modules.map(({ code }, index) => {
          const Card = Digit.ComponentRegistryService.getComponent(`${code}Card`) || (() => <React.Fragment />);
          return <Card key={index} />;
        })}
      </div>
    </div>
  );
};

export const AppHome = ({ modules }) => <EmployeeHome modules={modules} />;
