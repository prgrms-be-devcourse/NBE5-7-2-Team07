"use client"

import { useState, useEffect } from "react"
import { Link, useParams, useLocation } from "react-router-dom"
import { SettlementList } from "../../components/settlement/settlement-list"
import { SettlementFilter } from "../../components/settlement/settlement-filter"
import { getAllSettlements } from "../../service/settlementService"
import { useToast } from "../../context/ToastContext"

export function TeamSettlementsPage() {
  const { teamId } = useParams()
  const location = useLocation()
  const { addToast } = useToast()
  const [settlements, setSettlements] = useState([])
  const [users, setUsers] = useState([])
  const [expenses, setExpenses] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState(null)

  // URL 쿼리 파라미터 파싱
  const searchParams = new URLSearchParams(location.search)
  const filters = {
    payerId: searchParams.get("payerId") || "",
    settlerId: searchParams.get("settlerId") || "",
    expenseId: searchParams.get("expenseId") || "",
    isSettled: searchParams.get("isSettled") || "",
  }

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true)
        const data = await getAllSettlements(filters)
        setSettlements(data.settlements)
        setUsers(data.users)
        setExpenses(data.expenses)
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
  }, [location.search, addToast])

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
        <SettlementFilter users={users} expenses={expenses} initialFilters={filters} teamId={teamId} />
      </div>

      <SettlementList settlements={settlements} />
    </div>
  )
}
