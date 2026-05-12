export type AdminDashboardSummary = {
  totalUsers: number
  totalBooks: number
  totalCategories: number
  totalAuthors: number
  totalChapters: number
  totalViews: number
  totalFavorites: number
  totalComments: number
  totalReports: number
  pendingReports: number
  totalRevenue: number
}

export type AdminMonthlyStats = {
  month: string
  newUsers: number
  newBooks: number
  newChapters: number
  views: number
  comments: number
  revenue: number
}
