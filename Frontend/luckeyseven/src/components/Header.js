import React from 'react';
import { Link } from 'react-router-dom';
import styles from '../styles/Header.module.css';

const Header = () => {
  return (
      <header className={styles.header}>
        <div className={styles.headerLogo}>
          <span className={styles.headerIcon}>🌍</span>
          <h1 className={styles.headerTitle}>Travel Expense Manager</h1>
        </div>
        <nav className={styles.headerNav}>
          <Link to="/team-setup" className={styles.navLink}>Team Setup</Link>
          <a href="/TeamDashboard" className={styles.navLink}>Dashboard</a>
          <a href="/logout" className={styles.navLink}>Logout</a>
        </nav>
      </header>
  );
};

export default Header;
