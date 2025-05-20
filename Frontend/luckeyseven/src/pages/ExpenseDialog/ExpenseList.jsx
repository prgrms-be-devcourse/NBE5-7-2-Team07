import React, {useState, useEffect, useCallback} from 'react';
import {useNavigate} from 'react-router-dom';
import AddExpenseDialog from './AddExpenseDialog';
import ExpenseDetailDialog from './ExpenseDetailDialog';
import '../../components/styles/expenseList.css';
import {getListExpense} from '../../service/ExpenseService';
import {FaMoneyBillWave} from 'react-icons/fa';
import Header from '../../components/Header';

import {FiHome} from 'react-icons/fi';
import {FiPlus} from 'react-icons/fi';
import {useRecoilValue} from "recoil";
import {currentTeamIdState} from "../../recoil/atoms/teamAtoms";

const CATEGORY_LABELS = {
  MEAL: '식사',
  SNACK: '간식',
  TRANSPORT: '교통',
  ACCOMMODATION: '숙박',
  MISCELLANEOUS: '기타',
};

export default function ExpenseList() {
  const teamId = useRecoilValue(currentTeamIdState);
  const navigate = useNavigate();
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [selectedExpenseId, setSelectedExpenseId] = useState(null);

  const [expenses, setExpenses] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [sortDirection, setSortDirection] = useState('DESC');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [balances, setBalances] = useState(null);
  const [notification, setNotification] = useState({message: '', type: ''});

  const fetchExpenses = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getListExpense(teamId, page, size,
          `createdAt,${sortDirection}`);
      setExpenses(data.content);
      setTotalPages(data.totalPages);
      setError(null);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  }, [teamId, page, size, sortDirection]);

  useEffect(() => {
    fetchExpenses();
  }, [fetchExpenses]);

  useEffect(() => {
    if (!balances && !notification.message) {
      return;
    }
    const timer = setTimeout(() => {
      setBalances(null);
      setNotification({message: '', type: ''});
    }, 10000);
    return () => clearTimeout(timer);
  }, [balances, notification]);

  if (loading) {
    return (
        <div className="expense-tracker">
          <Header/>
          <div className="content">
            <div className="loading">데이터를 불러오고 있습니다...</div>
          </div>
        </div>
    );
  }

  if (error) {
    return (
        <div className="expense-tracker">
          <Header/>
          <div className="content">
            <div className="error">
              <p>데이터를 불러오는 중 오류가 발생했습니다</p>
              <p>{error.message}</p>
            </div>
          </div>
        </div>
    );
  }

  const openDetail = (expenseId) => setSelectedExpenseId(expenseId);
  const closeDetail = () => setSelectedExpenseId(null);
  const goToPage = (pageNumber) => setPage(pageNumber - 1);

  const handleAddSuccess = async (newExpense, balancesObj) => {
    setBalances(balancesObj);
    setNotification({message: '지출이 성공적으로 등록되었습니다.', type: 'register'});
    setShowAddDialog(false);
    try {
      await fetchExpenses();
    } catch (err) {
      console.error('지출 리스트 재조회 실패:', err);
    }
  };

  const handleUpdateSuccess = (updatedExpense, balancesObj) => {
    setExpenses(prev =>
        prev.map(exp =>
            exp.id === updatedExpense.id
                ? {...updatedExpense}
                : exp
        )
    );
    setBalances(balancesObj);
    setNotification({message: '지출이 성공적으로 수정되었습니다.', type: 'update'});
    closeDetail();
  };

  const handleDeleteSuccess = (deletedId, balancesObj) => {
    setExpenses(prev => prev.filter(exp => exp.id !== deletedId));
    setBalances(balancesObj);
    setNotification({message: '지출이 성공적으로 삭제되었습니다.', type: 'delete'});
    closeDetail();
  };

  const fmt = (value) => (value != null ? value.toLocaleString() : '-');

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      weekday: 'short'
    });
  };

  return (
      <div className="expense-tracker">
        <Header/>
        <div className="content">
          <h2 className="section-title">
            <FaMoneyBillWave className="section-icon"/>
            지출 내역
          </h2>

          {(balances || notification.message) && (
              <div className="balance-banner">
                {balances && (
                    <div>
                      <span className="label">원화 잔고:</span>
                      <strong>₩{fmt(balances.balance)}</strong>
                      &nbsp;&nbsp;
                      <span className="label">외화 잔고:</span>
                      <strong>${fmt(balances.foreignBalance)}</strong>
                    </div>
                )}

                <div className="actions">

                  <div className="header-actions">
                    {/* <button className="btn btn-outlined">예산 수정</button> */}
                    <button className="btn btn-filled"
                            onClick={() => setShowAddDialog(true)}>지출 추가
                    </button>
                  </div>


                  <div className="sort-control">
                    <button
                        className="sort-btn"
                        onClick={() => setSortDirection(
                            prev => prev === 'DESC' ? 'ASC' : 'DESC')}
                    >
                      날짜순 <span className="icon">{sortDirection === 'DESC' ? '↓'
                        : '↑'}</span>
                    </button>
                  </div>
                </div>

                {expenses.length === 0 ? (
                    <div className="empty-state">
                      <h3>지출 내역이 없습니다</h3>
                      <p>'지출 추가' 버튼을 클릭하여 첫 지출을 등록해보세요.</p>
                    </div>
                ) : (
                    <div className="expense-table">
                      <table>
                        <thead>
                        <tr>
                          <th>제목</th>
                          <th>가격 (KRW)</th>
                          <th>카테고리</th>
                          <th>날짜</th>
                          <th>결제자</th>
                        </tr>
                        </thead>
                        <tbody>
                        {expenses.map(exp => (
                            <tr key={exp.id} onClick={() => openDetail(exp.id)}
                                style={{cursor: 'pointer'}}>
                              <td>{exp.description}</td>
                              <td className="amount">₩{exp.amount.toLocaleString()}</td>
                              <td><span className="category"
                                        data-category={exp.category}>{CATEGORY_LABELS[exp.category]
                                  || exp.category}</span></td>
                              <td>{formatDate(exp.createdAt)}</td>
                              <td>{exp.payerNickname}</td>
                            </tr>
                        ))}
                        </tbody>
                      </table>
                    </div>
                )}
              </div>
          )}

          <div className="actions">
            <div className="header-actions">
              <button
                  className="btn btn-outlined"
                  onClick={() => navigate('/TeamDashBoard')}
              >
                <FiHome style={{marginRight: '4px'}}/> 팀 대시보드
              </button>
              <button
                  className="btn btn-filled"
                  onClick={() => setShowAddDialog(true)}
              >
                <FiPlus style={{marginRight: '4px'}}/> 지출 추가
              </button>
            </div>

            <div className="sort-control">
              <button
                  className="sort-btn"
                  onClick={() => setSortDirection(
                      prev => prev === 'DESC' ? 'ASC' : 'DESC')}
              >
                날짜순 <span className="icon">{sortDirection === 'DESC' ? '↓'
                  : '↑'}</span>
              </button>
            </div>
          </div>

          {expenses.length === 0 ? (
              <div className="empty-state">
                <h3>지출 내역이 없습니다</h3>
                <p>'지출 추가' 버튼을 클릭하여 첫 지출을 등록해보세요.</p>
              </div>
          ) : (
              <div className="expense-table">
                <table>
                  <thead>
                  <tr>
                    <th>제목</th>
                    <th>가격 (KRW)</th>
                    <th>카테고리</th>
                    <th>날짜</th>
                    <th>결제자</th>
                  </tr>
                  </thead>
                  <tbody>
                  {expenses.map(exp => (
                      <tr key={exp.id} onClick={() => openDetail(exp.id)}
                          style={{cursor: 'pointer'}}>
                        <td>{exp.description}</td>
                        <td className="amount">₩{exp.amount.toLocaleString()}</td>
                        <td><span className="category"
                                  data-category={exp.category}>{CATEGORY_LABELS[exp.category]
                            || exp.category}</span></td>
                        <td>{formatDate(exp.createdAt)}</td>
                        <td>{exp.payerNickname}</td>
                      </tr>
                  ))}
                  </tbody>
                </table>
              </div>
          )}

          {totalPages > 1 && (
              <div className="pagination">
                <button onClick={() => goToPage(page)} disabled={page === 0}>←
                </button>
                {Array.from({length: totalPages}, (_, i) => {
                  const pageNum = i + 1;
                  const currentPage = page + 1;
                  if (pageNum === 1 || pageNum === totalPages || (pageNum
                      >= currentPage - 1 && pageNum <= currentPage + 1)) {
                    return <button key={i}
                                   className={pageNum === currentPage ? 'active'
                                       : ''} onClick={() => goToPage(
                        pageNum)}>{pageNum}</button>;
                  }
                  if (pageNum === currentPage - 2 && currentPage
                      > 3) {
                    return <span key="ellip-1"
                                 className="pagination-ellipsis">...</span>;
                  }
                  if (pageNum === currentPage + 2 && currentPage < totalPages
                      - 2) {
                    return <span key="ellip-2"
                                 className="pagination-ellipsis">...</span>;
                  }
                  return null;
                })}
                <button onClick={() => goToPage(page + 2)}
                        disabled={page + 1 === totalPages}>→
                </button>
              </div>
          )}
          {totalPages > 1 && (
              <div className="pagination">
                <button onClick={() => goToPage(page)}
                        disabled={page === 0}>←
                </button>
                {Array.from({length: totalPages}, (_, i) => {
                  const pageNum = i + 1;
                  const currentPage = page + 1;
                  if (pageNum === 1 || pageNum === totalPages || (pageNum
                      >= currentPage - 1 && pageNum <= currentPage + 1)) {
                    return <button key={i} className={pageNum === currentPage
                        ? 'active' : ''} onClick={() => goToPage(
                        pageNum)}>{pageNum}</button>;
                  }
                  if (pageNum === currentPage - 2 && currentPage
                      > 3) {
                    return <span key="ellip-1"
                                 className="pagination-ellipsis">...</span>;
                  }
                  if (pageNum === currentPage + 2 && currentPage < totalPages
                      - 2) {
                    return <span key="ellip-2"
                                 className="pagination-ellipsis">...</span>;
                  }
                  return null;
                })}
                <button onClick={() => goToPage(page + 2)}
                        disabled={page + 1 === totalPages}>→
                </button>
              </div>
          )}

          {showAddDialog && <AddExpenseDialog
              onClose={() => setShowAddDialog(false)}
              onSuccess={handleAddSuccess}/>}
          {selectedExpenseId && <ExpenseDetailDialog
              expenseId={selectedExpenseId} onClose={closeDetail}
              onUpdate={handleUpdateSuccess} onDelete={handleDeleteSuccess}/>}
        </div>
      </div>
  );
}