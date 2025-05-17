import { atom } from 'recoil';
import { recoilPersist } from "recoil-persist";

// export const currentTeamIdState = atom({
//   key: 'currentTeamIdState', // unique ID (with respect to other atoms/selectors)
//   default: 1, // default value (aka initial value)
// });

const { persistAtom } = recoilPersist();
export const currentTeamIdState = atom({
  key: "currentTeamIdState",
  default: null, // 최초엔 null, 추후 URL로부터 세팅
  effects_UNSTABLE: [persistAtom], // persistAtom은 recoil-persist에서 제공하는 함수로, atom의 상태를 localStorage에 저장합니다.
});