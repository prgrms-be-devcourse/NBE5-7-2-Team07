import React from 'react';
import styles from '../styles/Header.module.css';

const Header = () => {
  return (
      <header className={styles.header}>
        <div className={styles.headerLogo}>
          <span className={styles.headerIcon}>🌍</span>
          <h1 className={styles.headerTitle}>Travel Expense Manager</h1>
        </div>
        <nav className={styles.headerNav}>
          <a href="#dashboard" className={styles.navLink}>Dashboard</a>
          <a href="#logout" className={styles.navLink}>Logout</a>
        </nav>
      </header>
  );
};

export default Header;
