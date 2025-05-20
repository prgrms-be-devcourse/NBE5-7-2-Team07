"use client"

import {useEffect, useState} from "react"
import {Link, useLocation, useNavigate, useParams} from "react-router-dom"
import {SettlementList} from "../../components/settlement/settlement-list"
import {SettlementFilter} from "../../components/settlement/settlement-filter"
import {getListSettlements, getUsers} from "../../service/settlementService"
import {useToast} from "../../context/ToastContext"
import {getAllExpense} from "../../service/ExpenseService";

export function TeamSettlementsPage() {
  const {teamId} = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const {addToast} = useToast()

  const [settlements, setSettlements] = useState([])
  const [users, setUsers] = useState([])
  const [expenses, setExpenses] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState(null)

  // 페이징 관련 상태
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  // URL 쿼리 파라미터 파싱
  const searchParams = new URLSearchParams(location.search)
  const filters = {
    payerId: searchParams.get("payerId") || "",
    settlerId: searchParams.get("settlerId") || "",
    expenseId: searchParams.get("expenseId") || "",
    isSettled: searchParams.get("isSettled") || "",
  }

  // 페이징 정보 파싱
  const page = parseInt(searchParams.get("page") || "0", 10)
  const size = parseInt(searchParams.get("size") || "10", 10)
  const sort = searchParams.get("sort") || "createdAt,DESC"

  // 페이지 변경 핸들러
  const handlePageChange = (newPage) => {
    const params = new URLSearchParams(location.search)
    params.set("page", newPage.toString())
    navigate(`${location.pathname}?${params.toString()}`)
  }

  // 페이지 크기 변경 핸들러
  const handlePageSizeChange = (newSize) => {
    const params = new URLSearchParams(location.search)
    params.set("size", newSize.toString())
    params.set("page", "0") // 페이지 크기 변경 시 첫 페이지로 돌아가기
    navigate(`${location.pathname}?${params.toString()}`)
  }

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true)
        const settlementResponse = await getListSettlements(teamId, page, size,
            sort, filters)
        setSettlements(settlementResponse.content)
        const usersResponse = await getUsers(teamId)
        setUsers(usersResponse)
        const expensesResponse = await getAllExpense(teamId)
        setExpenses(expensesResponse)

        // 페이징 메타데이터 설정
        setTotalPages(settlements.totalPages)
        setTotalElements(settlements.totalElements)
      } catch (error) {
        console.error("팀 정산 내역 조회 오류:", error)
        setError(error.message)
        addToast({
          title: "오류 발생",
          description: "팀 정산 내역을 불러오는데 실패했습니다.",
          variant: "destructive",
        })
      } finally {
        setIsLoading(false)
      }
    }

    fetchData()
  }, [teamId, page, size, sort, location.search, addToast])

  if (isLoading) {
    return (
        <div className="container py-8">
          <div className="flex justify-center items-center h-64">
            <p>로딩 중...</p>
          </div>
        </div>
    )
  }

  if (error) {
    return (
        <div className="container py-8">
          <div className="flex flex-col justify-center items-center h-64">
            <p className="text-lg font-medium text-error">오류가 발생했습니다.</p>
            <p className="text-muted">{error}</p>
          </div>
        </div>
    )
  }

  return (
      <div className="container py-8">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold">팀 정산 내역</h1>
          <Link to="/settlements/new">
            <button className="btn btn-primary">새 정산 생성</button>
          </Link>
        </div>

        <div className="mb-6">
          <SettlementFilter users={users} expenses={expenses}
                            initialFilters={filters} teamId={teamId}/>
        </div>

        <SettlementList settlements={settlements}/>

        {/* 페이지네이션 컴포넌트 */}
        <div className="flex justify-between items-center mt-6">
          <div className="text-sm text-muted">
            총 {totalElements}개 항목 중 {page * size + 1}-{Math.min(
              (page + 1) * size, totalElements)}
          </div>

          <div className="flex gap-2">
            <select
                className="select select-sm select-bordered"
                value={size}
                onChange={(e) => handlePageSizeChange(
                    parseInt(e.target.value, 10))}
            >
              <option value="5">5개씩 보기</option>
              <option value="10">10개씩 보기</option>
              <option value="20">20개씩 보기</option>
              <option value="50">50개씩 보기</option>
            </select>

            <div className="join">
              <button
                  className="join-item btn btn-sm"
                  disabled={page === 0}
                  onClick={() => handlePageChange(0)}
              >
                «
              </button>
              <button
                  className="join-item btn btn-sm"
                  disabled={page === 0}
                  onClick={() => handlePageChange(page - 1)}
              >
                ‹
              </button>

              {/* 페이지 번호 버튼 */}
              {Array.from({length: Math.min(5, totalPages)}, (_, i) => {
                const pageNum = page - 2 + i

                // 음수 페이지 번호는 표시하지 않음
                if (pageNum < 0) {
                  return null
                }

                // 총 페이지 수를 초과하는 페이지 번호는 표시하지 않음
                if (pageNum >= totalPages) {
                  return null
                }

                return (
                    <button
                        key={pageNum}
                        className={`join-item btn btn-sm ${pageNum === page
                            ? 'btn-active' : ''}`}
                        onClick={() => handlePageChange(pageNum)}
                    >
                      {pageNum + 1}
                    </button>
                )
              })}

              <button
                  className="join-item btn btn-sm"
                  disabled={page >= totalPages - 1}
                  onClick={() => handlePageChange(page + 1)}
              >
                ›
              </button>
              <button
                  className="join-item btn btn-sm"
                  disabled={page >= totalPages - 1}
                  onClick={() => handlePageChange(totalPages - 1)}
              >
                »
              </button>
            </div>
          </div>
        </div>
      </div>
  )
}