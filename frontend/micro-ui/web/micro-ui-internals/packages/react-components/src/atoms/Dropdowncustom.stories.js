import React from "react";

import Dropdowncustom from "./Dropdowncustom";

export default {
  title: "Atom/Dropdowncustom",
  component: Dropdowncustom,
};

const Template = (args) => <Dropdowncustom {...args} />;

export const Default = Template.bind({});

Default.args = {
  selected: "first",
  option: ["first", "second", "third"],
  optionKey: 0,
};
