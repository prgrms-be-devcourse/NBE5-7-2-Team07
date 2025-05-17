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
          <button className={styles.buttonSecondary}>예산 추가</button>
          <button className={styles.buttonPrimary}>예산 수정</button>
        </div>
      </div>
  );
};

export default PageHeaderControls;
