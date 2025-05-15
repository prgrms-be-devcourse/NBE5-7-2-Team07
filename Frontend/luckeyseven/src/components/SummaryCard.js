import React from 'react';
import styles from '../styles/SummaryCard.module.css';

const SummaryCard = ({ title, amount, currency, percentage, of }) => {
  return (
      <div className={styles.summaryCard}>
        <p className={styles.cardTitle}>{title}</p>
        <h3 className={styles.cardAmount}>{currency}{amount.toLocaleString()}</h3>
        {percentage && <p className={styles.cardPercentage}>{percentage}% {of}</p>}
      </div>
  );
};

export default SummaryCard;
