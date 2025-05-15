import React from 'react';
import styles from '../styles/PageHeaderControls.module.css';

const PageHeaderControls = () => {
  return (
      <div className={styles.pageHeaderControls}>
        <div>
          <h2 className={styles.pageTitle}>Japan Trip 25</h2>
          <p className={styles.pageSubtitle}>Manage your team's expenses and budget</p>
        </div>
        <div className={styles.pageActions}>
          <button className={styles.buttonSecondary}>Edit Budget</button>
          <button className={styles.buttonPrimary}>Add Expense</button>
        </div>
      </div>
  );
};

export default PageHeaderControls;
