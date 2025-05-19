import React from 'react';
import { Link } from 'react-router-dom';
import { useRecoilValue } from 'recoil';
import { currentTeamIdState } from '../recoil/atoms/teamAtoms';
import styles from '../styles/Tabs.module.css';

const Tabs = ({ activeTab, setActiveTab }) => {
  const teamId = useRecoilValue(currentTeamIdState);
  const tabs = ['Overview', 'Members', 'Expenses', 'Settlement'];

  const getTabPath = (tab) => {
    if (!teamId) return '#'; // Or handle the case where teamId is not available

    switch (tab) {
      case 'Expenses':
        return `/team/${teamId}/expenses`;
      case 'Settlement':
        return `/team/${teamId}/settlement`;
      default:
        return '#'; // Overview and Members will still use setActiveTab
    }
  };

  return (
      <div className={styles.tabs}>
        {tabs.map(tab => {
          const isLink = tab === 'Expenses' || tab === 'Settlement';
          const path = getTabPath(tab);

          return isLink ? (
            <Link
              key={tab}
              to={path}
              className={`${styles.tabButton} ${activeTab === tab ? styles.tabButtonActive : ''}`}
              onClick={() => setActiveTab(tab)} // Keep active tab state for styling
            >
              {tab}
            </Link>
          ) : (
            <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`${styles.tabButton} ${activeTab === tab ? styles.tabButtonActive : ''}`}
            >
              {tab}
            </button>
          );
        })}
      </div>
  );
};

export default Tabs;
