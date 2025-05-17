import { atom } from 'recoil';

export const currentTeamIdState = atom({
  key: 'currentTeamIdState', // unique ID (with respect to other atoms/selectors)
  default: 1, // default value (aka initial value)
});
