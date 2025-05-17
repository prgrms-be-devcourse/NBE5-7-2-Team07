import React, { useState, useEffect } from 'react';
import { useRecoilValue } from 'recoil';
import { currentTeamIdState } from '../recoil/atoms/teamAtoms';
import { getTeamDashboard, getTeamMembers } from '../service/ApiService';
import styles from '../styles/App.module.css'; // 수정됨
import Header from '../components/Header';
import PageHeaderControls from '../components/PageHeaderControls';
import Tabs from '../components/Tabs';
import OverviewTabContent from '../components/OverviewTabContent';
import MembersTabContent from '../components/MembersTabContent';


// Placeholder for chart - In a real app, use a library like recharts or chart.js
// 추후 반영
const DoughnutChartPlaceholder = () => (
    <div className={styles.doughnutChart}>
      Chart
    </div>
);

function TeamDashBoard() {
  const [activeTab, setActiveTab] = useState('Overview');
  const teamId = useRecoilValue(currentTeamIdState);
  const [dashboardData, setDashboardData] = useState(null);
  const [membersData, setMembersData] = useState({
    teamCode: '',
    teamPassword: '',
    members: [] 
  });

  useEffect(() => {
    const fetchData = async () => {
      if (teamId) {
        try {
          const overviewData = await getTeamDashboard(teamId);
          console.log("Overview Data:", overviewData);
          setDashboardData(overviewData);

          const { teamCode, teamPassword} = overviewData;
          
          const teamMembers = await getTeamMembers(teamId);
          
          console.log("Team Members Data:", teamMembers);
          setMembersData({
            teamCode,
            teamPassword,
            members: teamMembers
          });
        } catch (error) { 
          console.error("Error fetching team data:", error);
          // Handle error appropriately, e.g., show a notification
        }
      }
    };

    fetchData();
  }, [teamId]);

  return (
    <div className={styles.app}>
      <Header />
      <main className={styles.main}>
        <PageHeaderControls />
        <Tabs activeTab={activeTab} setActiveTab={setActiveTab} />
        {activeTab === 'Overview' && <OverviewTabContent dashboardData={dashboardData} />}
        {activeTab === 'Members' && <MembersTabContent teamCode={membersData.teamCode} teamPassword={membersData.teamPassword} members={membersData.members} />}
        {/* Expenses and Settlement tabs now navigate to separate pages */}
      </main>
    </div>
  );
}

export default TeamDashBoard;
