import { fetchWithAuth } from '../stores/auth'
import type { AdminDashboardSummary, AdminMonthlyStats } from '../types/dashboard'

export function getDashboardSummary() {
  return fetchWithAuth<AdminDashboardSummary>('/api/admin/dashboard/summary')
}

export function getMonthlyStats() {
  return fetchWithAuth<AdminMonthlyStats[]>('/api/admin/dashboard/monthly-stats')
}
