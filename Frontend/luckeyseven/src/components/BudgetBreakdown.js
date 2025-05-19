import React from 'react';
import styles from '../styles/BudgetBreakdown.module.css';

const BudgetBreakdown = ({ expenses }) => {
  const budgetData = {
    totalBudget: 3000000,
    totalExpenses: 1250000,
    remainingBudget: 1750000,
    budgetUsedPercentage: 42,
  };

  // Define categories for display order and names
  const displayCategories = [
    { name: 'Food & Drinks' },
    { name: 'Accommodation' },
    { name: 'Transportation' },
    { name: 'Entertainment' },
    { name: 'Shopping' },
  ];

  const categoryTotals = expenses.reduce((acc, expense) => {
    acc[expense.category] = (acc[expense.category] || 0) + expense.amount;
    return acc;
  }, {});

  return (
      <div className={styles.budgetBreakdown}>
        <h4 className={styles.sectionTitle}>Budget Breakdown</h4>
        <p className={styles.sectionSubtitle}>How your budget is being spent</p>
        <div className={styles.categoryBreakdownList}>
          {displayCategories.map(category => {
            const totalAmount = categoryTotals[category.name] || 0;
            if (totalAmount > 0) { // Only display categories with expenses
              return (
                  <div key={category.name} className={styles.categoryBreakdownItem}>
                    <span>{category.name}:</span>
                    <span>₩{totalAmount.toLocaleString()}</span>
                  </div>
              );
            }
            return null;
          })}
        </div>
        <div className={styles.budgetStats}>
          <div className={styles.budgetStatRow}>
            <span>Total Budget:</span>
            <span>₩{budgetData.totalBudget.toLocaleString()}</span>
          </div>
          <div className={styles.budgetStatRow}>
            <span>Total Expenses:</span>
            <span>₩{budgetData.totalExpenses.toLocaleString()}</span>
          </div>
          <div className={styles.budgetStatRow}>
            <span>Remaining Budget:</span>
            <span>₩{budgetData.remainingBudget.toLocaleString()}</span>
          </div>
          <div className={`${styles.budgetStatRow} ${styles.budgetStatRowBold}`}>
            <span>Budget Used:</span>
            <span>{budgetData.budgetUsedPercentage}%</span>
          </div>
        </div>
      </div>
  );
};

export default BudgetBreakdown;
