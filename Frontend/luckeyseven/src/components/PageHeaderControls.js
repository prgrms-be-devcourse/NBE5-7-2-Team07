import React from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import styles from '../styles/PageHeaderControls.module.css';

const PageHeaderControls = ({ pageHeaderData }) => {
  const navigate = useNavigate();
  const { teamName, teamId, openDialog } = pageHeaderData || {};

  const handleSetBudget = () => {
    if (typeof openDialog === 'function') {
      openDialog('set');
    } else {
      console.error('openDialog is not a function');
    }
  };

  const handleEditBudget = () => {
    if (typeof openDialog === 'function') {
      openDialog('edit');
    } else {
      console.error('openDialog is not a function');
    }
  };

  const handleAddBudget = () => {
    if (typeof openDialog === 'function') {
      openDialog('add');
    } else {
      console.error('openDialog is not a function');
    }
  };

  const handleDeleteBudget = async () => {
    if (!teamId) {
      console.error('teamId is missing');
      return;
    }

    // 사용자에게 삭제 확인 요청
    if (!window.confirm('정말로 예산을 삭제하시겠습니까?')) {
      return;
    }

    try {
      await axios.delete(`/api/teams/${teamId}/budget`);
      console.log('예산 삭제 완료');
      alert('예산이 성공적으로 삭제되었습니다.');
    } catch (error) {
      console.error('예산 삭제 실패:', error);
      // 에러 메시지 표시
      alert('예산 삭제 실패: ' + (error.response?.data?.message || error.message));
    }
  };

  return (
    <div className={styles.pageHeaderControls}>
      <div>
        <h2 className={styles.pageTitle}>{teamName || 'Team Dashboard'}</h2>
        <p className={styles.pageSubtitle}>Manage your team's expenses and budget</p>
      </div>
      <div className={styles.pageActions}>
        <button className={styles.buttonPrimary} onClick={handleSetBudget}>예산 설정</button>
        <button className={styles.buttonSecondary} onClick={handleEditBudget}>예산 수정</button>
        <button className={styles.buttonSecondary} onClick={handleAddBudget}>예산 추가</button>
        <button onClick={handleDeleteBudget}>예산 삭제</button>
      </div>
    </div>
  );
};

export default PageHeaderControls;