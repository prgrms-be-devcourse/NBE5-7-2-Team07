import React, {useEffect, useState} from 'react';
import {useRecoilValue} from 'recoil';
import {currentTeamIdState} from '../recoil/atoms/teamAtoms';
import {getTeamDashboard, getTeamMembers} from '../service/ApiService'
import React, { useState, useEffect } from 'react';
import { useRecoilValue } from 'recoil';
import { currentTeamIdState } from '../recoil/atoms/teamAtoms';
import { getTeamDashboard, getTeamMembers } from '../service/TeamService';
import styles from '../styles/App.module.css';
import Header from '../components/Header';
import PageHeaderControls from '../components/PageHeaderControls';
import Tabs from '../components/Tabs';
import OverviewTabContent from '../components/OverviewTabContent';
import MembersTabContent from '../components/MembersTabContent';
import SetBudgetDialog from '../pages/BudgetPage/components/set-budget-dialog';
import EditBudgetDialog
  from '../pages/BudgetPage/components/edit-budget-dialog';
import AddBudgetDialog from '../pages/BudgetPage/components/add-budget-dialog';

const DoughnutChartPlaceholder = () => (
    <div className={styles.doughnutChart}>
      Chart
    </div>
);

function TeamDashBoard() {
  const [activeTab, setActiveTab] = useState('Overview');
  const teamId = useRecoilValue(currentTeamIdState);
  const [dashboardData, setDashboardData] = useState(null);
  // Added state for dialog control
  const [dialogType, setDialogType] = useState(null); // 'set', 'edit', 'add', or null
  const [pageHeaderData, setPageHeaderData] = useState({
    teamName: '',
    openDialog: setDialogType // Pass the dialog opener function
  });
  const [membersData, setMembersData] = useState({
    teamCode: '',
    teamPassword: '',
    members: []
  });
  const [budgetData, setBudgetData] = useState(null);
  const [budgetInitialized, setBudgetInitialized] = useState(false); // 예산 초기화 여부 추적

  // Close dialog handler
  const handleCloseDialog = () => {
    setDialogType(null);
  };

  // Function to handle budget updates from dialogs
  const handleBudgetUpdate = (updatedBudget) => {
    setBudgetData(updatedBudget);
    setBudgetInitialized(true); // 예산이 설정되었음을 표시

    if (dashboardData) {
      setDashboardData({
        ...dashboardData,
        budget: updatedBudget,
        totalAmount: updatedBudget.totalAmount || 0,
        balance: updatedBudget.balance || 0,
        foreignBalance: updatedBudget.foreignBalance || 0,
        foreignCurrency: updatedBudget.foreignCurrency || 'KRW',
        avgExchangeRate: updatedBudget.avgExchangeRate || 0
      });
    }
    setDialogType(null); // Close the dialog
  };

  useEffect(() => {
        const fetchData = async () => {
          if (teamId) {
            try {
              const overviewData = await getTeamDashboard(teamId);
              console.log("Overview Data:", overviewData);
              console.log("Budget:",)
              // 예산 정보 확인
              // 예산 정보가 있거나 사용자가 예산을 설정한 경우
              setDashboardData({
                ...overviewData,
                budget: {
                  totalAmount: budgetData?.totalAmount ?? overviewData.totalAmount
                      ?? 0,
                  balance: budgetData?.balance ?? overviewData.balance ?? 0,
                  foreignBalance: budgetData?.foreignBalance
                      ?? overviewData.foreignBalance ?? 0,
                  foreignCurrency: budgetData?.foreignCurrency
                      ?? overviewData.foreignCurrency ?? 'KRW',
                  avgExchangeRate: budgetData?.avgExchangeRate
                      ?? overviewData.avgExchangeRate ?? 0,
                }
              });

              const {teamName, teamCode, teamPassword} = overviewData;

              const teamMembers = await getTeamMembers(teamId);

              console.log("Team Members Data:", teamMembers);
              setMembersData({
                teamCode,
                teamPassword,
                members: teamMembers
              });
              console.log("teamName:", teamName);
              setPageHeaderData({
                teamName,
                openDialog: setDialogType // Make sure this is included when updating state
              });
            } catch
                (error) {
              console.error("Error fetching team data:", error);
              // 에러가 409 Conflict인 경우, 아직 예산이 설정되지 않은 것으로 간주
              if (error.response && error.response.status === 409) {
                // 기본 대시보드 데이터 설정
                const basicDashboardData = {
                  team_id: teamId,
                  teamName: membersData.teamName || `Team ${teamId}`,
                  totalAmount: 0,
                  balance: 0,
                  foreignBalance: 0,
                  foreignCurrency: 'KRW',
                  expenseList: [],
                  avgExchangeRate: 0
                };
                setDashboardData(basicDashboardData);
              }
            }
          }
        };

        fetchData();
      }

      ,
      [teamId, budgetData, budgetInitialized]
  )
  ;

  return (
      <div className={styles.app}>
        <Header/>
        <main className={styles.main}>
          <PageHeaderControls pageHeaderData={pageHeaderData}/>
          <Tabs activeTab={activeTab} setActiveTab={setActiveTab}/>
          {activeTab === 'Overview' && <OverviewTabContent
              dashboardData={dashboardData}/>}
          {activeTab === 'Members' && (
              <MembersTabContent
                  teamCode={membersData.teamCode}
                  teamPassword={membersData.teamPassword}
                  members={membersData.members}
              />
          )}

          {/* Render dialogs based on dialog type */}
          {dialogType === 'set' && (
              <SetBudgetDialog
                  teamId={teamId}
                  closeDialog={handleCloseDialog}
                  onBudgetUpdate={handleBudgetUpdate}
              />
          )}
          {dialogType === 'edit' && (
              <EditBudgetDialog
                  teamId={teamId}
                  budgetId={dashboardData?.budget?.id}
                  closeDialog={handleCloseDialog}
                  onBudgetUpdate={handleBudgetUpdate}
              />
          )}
          {dialogType === 'add' && (
              <AddBudgetDialog
                  teamId={teamId}
                  closeDialog={handleCloseDialog}
                  onBudgetUpdate={handleBudgetUpdate}
              />
          )}
        </main>
      </div>
  );
}

export default TeamDashBoard;