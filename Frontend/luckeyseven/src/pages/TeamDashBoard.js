import React, { useState } from 'react';
import styles from '../styles/App.module.css'; // ✅ 수정됨
import Header from '../components/Header';
import PageHeaderControls from '../components/PageHeaderControls';
import Tabs from '../components/Tabs';
import OverviewTabContent from '../components/OverviewTabContent';
import MembersTabContent from '../components/MembersTabContent';


// Placeholder for chart - In a real app, use a library like recharts or chart.js
const DoughnutChartPlaceholder = () =>(
    <div className={styles.doughnutChart}>
      Chart
    </div>
);

// Placeholder for Members data - API나 상태 관리에서 가져오는 데이터. 실제 앱에서는 이 데이터를 사용하면 될거 같다.
const teamMembersData = [
  { id: 1, name: 'Member1', email: 'Member1@example.com', role: 'Leader' },
  { id: 2, name: 'Member2', email: 'Member2@example.com', role: 'Member' },
  { id: 3, name: 'Member3', email: 'Member3@example.com', role: 'Member' },
  // Add more members as needed
];


function TeamDashBoard() {
  const [activeTab, setActiveTab] = useState('Overview');

  return (
      <div className={styles.app}>
        <Header />
        <main className={styles.main}>
          <PageHeaderControls />
          <Tabs activeTab={activeTab} setActiveTab={setActiveTab} />
          {activeTab === 'Overview' && <OverviewTabContent />}
          {activeTab === 'Members' && <MembersTabContent members={teamMembersData} />}
          {/* Add other tab content components here based on activeTab */}
          {/* {activeTab === 'Expenses' && <ExpensesTabContent />} */}
          {/* etc. */}
        </main>
      </div>
  );
}

export default TeamDashBoard;
