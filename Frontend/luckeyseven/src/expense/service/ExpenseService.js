// src/service/ExpenseService.js
import axios from 'axios';

const BASE_URL = '/api';

/**
 * 팀의 지출 내역 페이지 조회
 * @param {number|string} teamId 
 * @param {number} page 0부터 시작
 * @param {number} size 페이지당 개수
 * @param {string} sort 정렬조건, ex) 'createdAt,DESC'
 * @returns {Promise<PageResponse<ExpenseResponse>>}
 */
export async function getListExpense(teamId, page = 0, size = 10, sort = 'createdAt,DESC') {
  const resp = await axios.get(`${BASE_URL}/${teamId}/expenses`, {
    params: { page, size, sort }
  });
  return resp.data;
}
