import React from 'react';
import styles from '../styles/Tabs.module.css';

const Tabs = ({ activeTab, setActiveTab }) => {
  const tabs = ['Overview', 'Expenses', 'Members', 'Settlement'];
  return (
      <div className={styles.tabs}>
        {tabs.map(tab => (
            <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`${styles.tabButton} ${activeTab === tab ? styles.tabButtonActive : ''}`}
            >
              {tab}
            </button>
        ))}
      </div>
  );
};

export default Tabs;
