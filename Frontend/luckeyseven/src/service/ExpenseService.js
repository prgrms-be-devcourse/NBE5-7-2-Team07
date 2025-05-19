// src/service/ExpenseService.js

import {privateApi} from "./ApiService"

const BASE_URL = '/api';

/**
 * 팀의 지출 내역 페이지 조회
 * @param {number|string} teamId
 * @param {number} page 0부터 시작
 * @param {number} size 페이지당 개수
 * @param {string} sort 정렬조건, ex) 'createdAt,DESC'
 * @returns {Promise<PageResponse<ExpenseResponse>>}
 */
export async function getListExpense(teamId, page = 0, size = 10,
    sort = 'createdAt,DESC') {
  const resp = await privateApi.get(`${BASE_URL}/${teamId}/expenses`, {
    params: {page, size, sort}
  });
  return resp.data;
}

/**
 * 팀의 모든 지출 내역을 페이징을 통해 전체 조회
 * @param {number|string} teamId
 * @param {string} sort 정렬조건, ex) 'createdAt,DESC'
 * @returns {Promise<Array<ExpenseResponse>>}
 */
export async function getAllExpense(teamId, sort = 'createdAt,DESC') {
  const allExpenses = [];
  let page = 0;
  const size = 50; // 한 번에 가져올 항목 수 (서버 설정에 맞게 조정)
  let hasMore = true;

  while (hasMore) {
    try {
      const response = await privateApi.get(`${BASE_URL}/${teamId}/expenses`, {
        params: {page, size, sort}
      });

      const {content, last} = response.data;
      
      if (!content || content.length === 0) {
        hasMore = false;
      } else {
        allExpenses.push(...content);

        hasMore = !last;
        page++;
      }
    } catch (error) {
      console.error(`지출 내역 전체 조회 오류 (페이지 ${page}):`, error);
      throw error;
    }
  }

  return allExpenses;
}