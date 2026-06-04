import { fetchWithAuth } from '../stores/auth'

export function getDashboardSummary() {
  return fetchWithAuth('/api/admin/dashboard/summary')
}

export function getMonthlyStats() {
  return fetchWithAuth('/api/admin/dashboard/monthly-stats')
}
