import { FSMService } from './services/elements/FSM';
import { initI18n } from '@egovernments/digit-ui-libraries/src/translations';
import Hooks from './hooks';

const setupLibraries = (Library, props) => {
  window.Digit = window.Digit || {};
  window.Digit[Library] = window.Digit[Library] || {};
  window.Digit[Library] = { ...window.Digit[Library], ...props };
};

const initFSMLibraries = () => {
  setupLibraries('Hooks', { ...window.Digit.Hooks, ...Hooks });
  setupLibraries('FSMService', FSMService);

  return new Promise((resolve) => {
    initI18n(resolve);
  });
};

export { initFSMLibraries, Hooks };
