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
import EditBudgetDialog from '../pages/BudgetPage/components/edit-budget-dialog';
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
  // 다이얼로그 인스턴스를 구분하기 위한 키 생성 상태 추가
  const [dialogKey, setDialogKey] = useState(Date.now());
  const [pageHeaderData, setPageHeaderData] = useState({
    teamName: '',
    openDialog: (type) => {
      // 다이얼로그 타입을 설정하고 새 키 생성
      setDialogType(type);
      setDialogKey(Date.now());
    }
  });
  const [membersData, setMembersData] = useState({
    teamCode: '',
    teamPassword: '',
    members: []
  });
  const [budgetData, setBudgetData] = useState(null);
  const [budgetInitialized, setBudgetInitialized] = useState(false);

  // Close dialog handler
  const handleCloseDialog = () => {
    setDialogType(null);
  };

  // Open dialog handler with new instance key
  const handleOpenDialog = (type) => {
    setDialogType(type);
    setDialogKey(Date.now()); // 새로운 키 생성하여 컴포넌트 재생성 보장
  };

  // Function to handle budget updates from dialogs
  const handleBudgetUpdate = (updatedBudget) => {
    setBudgetData(updatedBudget);
    setBudgetInitialized(true);

    if (dashboardData) {
      setDashboardData({
        ...dashboardData,
        budget: {
          ...dashboardData.budget,
          totalAmount: updatedBudget?.totalAmount || 0,
          balance: updatedBudget?.balance || 0,
          foreignBalance: updatedBudget?.foreignBalance || 0,
          foreignCurrency: updatedBudget?.foreignCurrency || 'KRW',
          avgExchangeRate: updatedBudget?.avgExchangeRate || 0
        }
      });
    }

    setDialogType(null);
  };

  // 예산 삭제 처리 함수 - 수정됨
  const handleBudgetDelete = async () => {
    // 예산 데이터 초기화
    setBudgetData(null);
    setBudgetInitialized(false);

    // 대시보드 정보에서 예산 정보를 0으로 리셋
    if (dashboardData) {
      setDashboardData({
        ...dashboardData,
        budget: {
          totalAmount: 0,
          balance: 0,
          foreignBalance: 0,
          foreignCurrency: 'KRW',
          avgExchangeRate: 0
        }
      });
    }

    // 모든 관련 대화상자 닫기
    setDialogType(null);

    // 필요하다면 대시보드 데이터를 새로 불러옴
    try {
      const refreshedData = await getTeamDashboard(teamId);
      setDashboardData({
        ...refreshedData,
        budget: {
          totalAmount: 0,
          balance: 0,
          foreignBalance: 0,
          foreignCurrency: 'KRW',
          avgExchangeRate: 0
        }
      });
    } catch (error) {
      console.error("대시보드 데이터 갱신 실패:", error);
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      if (teamId) {
        try {
          const overviewData = await getTeamDashboard(teamId);
          console.log("Overview Data:", overviewData);

          // Make sure budget values are never null by using nullish coalescing
          setDashboardData({
            ...overviewData,
            budget: {
              totalAmount: budgetData?.totalAmount ?? overviewData?.totalAmount ?? 0,
              balance: budgetData?.balance ?? overviewData?.balance ?? 0,
              foreignBalance: budgetData?.foreignBalance ?? overviewData?.foreignBalance ?? 0,
              foreignCurrency: budgetData?.foreignCurrency ?? overviewData?.foreignCurrency ?? 'KRW',
              avgExchangeRate: budgetData?.avgExchangeRate ?? overviewData?.avgExchangeRate ?? 0,
            }
          });

          const { teamName, teamCode, teamPassword } = overviewData || {};

          const teamMembers = await getTeamMembers(teamId);

          console.log("Team Members Data:", teamMembers);
          setMembersData({
            teamCode: teamCode || '',
            teamPassword: teamPassword || '',
            members: teamMembers || []
          });
          console.log("teamName:", teamName);
          setPageHeaderData({
            teamName: teamName || `Team ${teamId}`,
            openDialog: handleOpenDialog
          });
        } catch (error) {
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
  }, [teamId, budgetData, budgetInitialized]);

  return (
    <div className={styles.app}>
      <Header />
      <main className={styles.main}>
        <PageHeaderControls
          pageHeaderData={pageHeaderData}
          onBudgetDelete={handleBudgetDelete}
        />
        <Tabs activeTab={activeTab} setActiveTab={setActiveTab} />
        {activeTab === 'Overview' &&
          <OverviewTabContent dashboardData={dashboardData} />
        }
        {activeTab === 'Members' && (
          <MembersTabContent
            teamCode={membersData.teamCode}
            teamPassword={membersData.teamPassword}
            members={membersData.members}
          />
        )}

        {/* Render dialogs based on dialog type with consistent key */}
        {dialogType === 'set' && (
          <SetBudgetDialog
            key={`set-budget-${dialogKey}`}
            teamId={teamId}
            closeDialog={handleCloseDialog}
            onBudgetUpdate={handleBudgetUpdate}
          />
        )}
        {dialogType === 'edit' && (
          <EditBudgetDialog
            key={`edit-budget-${dialogKey}`}
            teamId={teamId}
            budgetId={dashboardData?.budget?.id}
            closeDialog={handleCloseDialog}
            onBudgetUpdate={handleBudgetUpdate}
          />
        )}
        {dialogType === 'add' && (
          <AddBudgetDialog
            key={`add-budget-${dialogKey}`}
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