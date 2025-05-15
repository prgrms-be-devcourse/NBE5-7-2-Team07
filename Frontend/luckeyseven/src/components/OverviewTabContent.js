import React from 'react';
import styles from '../styles/App.module.css'; // Keep App.module.css for general layout styles
import SummaryCard from '../components/SummaryCard';
import BudgetBreakdown from '../components/BudgetBreakdown';
import RecentExpensesTable from '../components/RecentExpenseTable';

const OverviewTabContent = () => {
  // This data would ideally be lifted to a higher state (e.g., in App component)
  const expensesData = [
    { title: 'Dinner at Sushi Restaurant', amount: 220000, jpy: 13200, category: 'Food & Drinks', date: '2025-05-05', paidBy: 'John Doe' },
    { title: 'Tokyo Metro Day Pass', amount: 28000, jpy: 2000, category: 'Transportation', date: '2025-05-04', paidBy: 'Jane Smith' },
    { title: 'Souvenir Shopping', amount: 90000, jpy: 10000, category: 'Shopping', date: '2025-05-03', paidBy: 'John Doe' },
    { title: 'Tokyo Skytree Tickets', amount: 65000, jpy: 5000, category: 'Entertainment', date: '2025-05-02', paidBy: 'Mike Johnson' },
    { title: 'Hotel Accommodation', amount: 650000, jpy: 50000, category: 'Accommodation', date: '2025-05-01', paidBy: 'John Doe' },
  ];
  // Ensure category names in expensesData match those in displayCategories in BudgetBreakdown
  // e.g., 'Food' in expensesData should be 'Food & Drinks' if that's what displayCategories uses.
  // For this example, I've updated 'Food' to 'Food & Drinks' in expensesData.

  return (
      <div>
        <div className={styles.summaryCardContainer}>
          <SummaryCard title="총 예산" amount={3000000} currency="₩" />
          <SummaryCard title="총 지출" amount={1250000} currency="₩" percentage={41.7} of="of budget" />
          <SummaryCard title="남은 예산" amount={1750000} currency="₩" percentage={58.3} of="of budget" />
          <SummaryCard title="남은 외화" amount={1750000} currency="$" percentage={58.3} of="of budget" />
        </div>
        <div className={styles.detailsContainer}>
          <BudgetBreakdown expenses={expensesData} />
          <RecentExpensesTable expenses={expensesData} />
        </div>
      </div>
  );
};

export default OverviewTabContent;
