import React from 'react';
import styles from '../styles/RecentExpensesTable.module.css';

const RecentExpensesTable = ({ expenses }) => {
  const categoryColors = {
    'Food & Drinks': '#4CAF50',
    Transportation: '#2196F3',
    Shopping: '#9C27B0',
    Entertainment: '#E91E63',
    Accommodation: '#FFC107', // Assuming a color for accommodation
  };

  return (
      <div className={styles.recentExpenses}>
        <h4 className={styles.sectionTitle}>Recent Expenses</h4>
        <p className={styles.sectionSubtitle}>Latest team expenses</p>
        <table className={styles.expensesTable}>
          <thead>
          <tr>
            <th>Title</th>
            <th>Amount</th>
            <th>Category</th>
            <th>Date</th>
            <th>Paid By</th>
          </tr>
          </thead>
          <tbody>
          {expenses.map((exp, index) => (
              <tr key={index}>
                <td data-label="Title">{exp.title}</td>
                <td data-label="Amount">
                  <div className={styles.expenseAmount}>₩{exp.amount.toLocaleString()}</div>
                  <div className={styles.expenseAmountSecondary}>{exp.jpy.toLocaleString()} JPY</div>
                </td>
                <td data-label="Category">
                <span
                    className={styles.expenseCategory}
                    style={{ backgroundColor: categoryColors[exp.category] || '#ccc' }}
                >
                  {exp.category}
                </span>
                </td>
                <td data-label="Date">{exp.date}</td>
                <td data-label="Paid By">{exp.paidBy}</td>
              </tr>
          ))}
          </tbody>
        </table>
      </div>
  );
};

export default RecentExpensesTable;
