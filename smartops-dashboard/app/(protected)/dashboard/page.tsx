
'use client'

import { useServices } from '@/hooks/use-services'
import { useInsights } from '@/hooks/use-insights'
import { useState } from 'react'
import api from '@/service/api'

export default function DashboardPage() {
  const [selectedInsight, setSelectedInsight] = useState<string | null>(null)
const [deletingInsight, setDeletingInsight] = useState(false)
const [deleteError, setDeleteError] = useState('')

const handleDeleteInsight = async () => {
  if (!selectedInsight) return

  try {
    setDeletingInsight(true)
    setDeleteError('')

    await api.delete(`/api/insights/${selectedInsight}`)

    setLatestInsights((prev) =>
      prev.filter((insight) => insight.id !== selectedInsight)
    )

    setSelectedInsight(null)

  } catch (error) {
    console.error('Error deleting insight:', error)
    setDeleteError('Failed to delete insight. Please try again.')
  } finally {
    setDeletingInsight(false)
  }
}
  const {
    services,
    loading: servicesLoading,
  } = useServices()

  const {
  insights,
  setInsights: setLatestInsights,
  loading: insightsLoading,
  error: insightsError,
} = useInsights()

  const onlineServices = services.filter(
    (service) => service.status === 'UP'
  ).length

  const downServices = services.filter(
    (service) => service.status === 'DOWN'
  ).length

  const latestInsights = [...insights]
    .sort(
      (a, b) =>
        new Date(b.createdAt).getTime() -
        new Date(a.createdAt).getTime()
    )
    .slice(0, 3)

  const severityStyle = (severity: string) => {
    switch (severity?.toUpperCase()) {
      case 'CRITICAL':
        return 'bg-red-500/10 text-red-400 border-red-500/30'
      case 'WARNING':
        return 'bg-yellow-500/10 text-yellow-400 border-yellow-500/30'
      default:
        return 'bg-blue-500/10 text-blue-400 border-blue-500/30'
    }
  }

  return (
    <div className="min-h-screen bg-[#020817] text-white p-6 space-y-6">

      {/* HEADER */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold">
            SmartOps Dashboard
          </h1>
          <p className="text-slate-400 mt-1">
            Monitor services and analyze incidents with Gemini AI
          </p>
        </div>

        <div className="px-4 py-2 rounded-full border border-green-500/30 bg-green-500/10 text-green-400 text-sm">
          ● Monitoring Active
        </div>
      </div>

      {/* SERVICE SUMMARY */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">

        <div className="rounded-2xl border border-slate-800 bg-[#071226] p-5">
          <p className="text-sm text-slate-400">
            Total Services
          </p>
          <h2 className="mt-3 text-3xl font-bold">
            {servicesLoading ? '...' : services.length}
          </h2>
        </div>

        <div className="rounded-2xl border border-slate-800 bg-[#071226] p-5">
          <p className="text-sm text-slate-400">
            Services UP
          </p>
          <h2 className="mt-3 text-3xl font-bold text-green-400">
            {servicesLoading ? '...' : onlineServices}
          </h2>
        </div>

        <div className="rounded-2xl border border-slate-800 bg-[#071226] p-5">
          <p className="text-sm text-slate-400">
            Services DOWN
          </p>
          <h2 className="mt-3 text-3xl font-bold text-red-400">
            {servicesLoading ? '...' : downServices}
          </h2>
        </div>

      </div>

      {/* MONITORED SERVICES */}
      <section className="rounded-2xl border border-slate-800 bg-[#071226] p-6">

        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-lg font-semibold">
              Monitored Services
            </h2>
            <p className="text-sm text-slate-400">
              Current health status of your services
            </p>
          </div>

          <span className="text-sm text-green-400">
            {onlineServices} Online
          </span>
        </div>

        {servicesLoading ? (
          <p className="text-slate-400">Loading services...</p>
        ) : services.length === 0 ? (
          <div className="text-center py-12 text-slate-500">
            No services added yet.
          </div>
        ) : (
          <div className="space-y-3">
            {services.map((service) => (
              <div
                key={service.id}
                className="flex items-center justify-between gap-4 rounded-xl border border-slate-800 bg-[#0b172c] p-4"
              >
                <div className="flex items-center gap-4">
                  <div
                    className={`h-3 w-3 rounded-full ${
                      service.status === 'UP'
                        ? 'bg-green-500'
                        : service.status === 'DOWN'
                          ? 'bg-red-500'
                          : 'bg-yellow-500'
                    }`}
                  />

                  <div>
                    <h3 className="font-medium">
                      {service.serviceName}
                    </h3>
                    <p className="text-sm text-slate-400">
                      {service.status === 'UP'
                        ? 'Operational'
                        : service.status === 'DOWN'
                          ? 'Service Down'
                          : 'Checking status'}
                    </p>
                  </div>
                </div>

                <div className="text-right">
                  <p className="font-semibold">
                    {service.responseTime > 0
                      ? `${service.responseTime} ms`
                      : 'N/A'}
                  </p>
                  <p className="text-xs text-slate-500">
                    Response Time
                  </p>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* GEMINI AI INSIGHTS */}
      <section className="rounded-2xl border border-blue-500/20 bg-[#071226] p-6">

  {/* HEADER */}
  <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-6">
    <div>
      <h2 className="text-xl font-semibold">
        AI Insights
      </h2>

      <p className="text-sm text-slate-400 mt-1">
        AI-powered incident analysis generated by Gemini
      </p>
    </div>

    <span className="px-3 py-1 rounded-full border border-blue-500/30 bg-blue-500/10 text-blue-400 text-sm">
      ✦ Gemini AI
    </span>
  </div>

  {/* LOADING */}
  {insightsLoading ? (

    <div className="text-slate-400 py-8 text-center">
      Loading AI insights...
    </div>

  ) : insightsError ? (

    <div className="text-red-400 py-8 text-center">
      Failed to load AI insights.
    </div>

  ) : latestInsights.length === 0 ? (

    /* EMPTY STATE */
    <div className="text-center py-12">
      <p className="text-slate-300 font-medium">
        No AI insights available yet
      </p>

      <p className="text-sm text-slate-500 mt-2">
        Gemini analysis will appear here when a critical alert is generated.
      </p>
    </div>

  ) : (

    /* INSIGHT CARDS */
    <div className="space-y-5">

      {latestInsights.map((insight) => (

        <article
          key={insight.id}
          className="rounded-xl border border-slate-800 bg-[#0b172c] p-5 space-y-4"
        >

          {/* CARD HEADER */}
          <div className="flex items-start justify-between gap-3">

            <div>
              <h3 className="text-lg font-semibold">
                {insight.serviceName}
              </h3>

              <p className="text-xs text-slate-500 mt-1">
                {insight.createdAt
                  ? new Date(insight.createdAt).toLocaleString()
                  : 'Date unavailable'}
              </p>
            </div>

            <div className="flex items-center gap-3">

              <span
                className={`w-fit px-3 py-1 rounded-full border text-xs font-semibold ${severityStyle(insight.severity)}`}
              >
                {insight.severity}
              </span>

              {/* DELETE BUTTON */}
              <button
                type="button"
                onClick={() => {
                  setSelectedInsight(insight.id)
                  setDeleteError('')
                }}
                title="Delete insight"
                className="w-8 h-8 flex items-center justify-center rounded-lg
                           text-slate-400 hover:text-red-400
                           hover:bg-red-500/10 transition"
              >
                <span className="text-2xl leading-none">
                  ×
                </span>
              </button>

            </div>
          </div>

          {/* AI SUMMARY */}
          <div>
            <h4 className="text-sm font-semibold text-blue-400 mb-1">
              AI Summary
            </h4>

            <p className="text-sm text-slate-300 leading-6">
              {insight.summary || 'No summary available.'}
            </p>
          </div>

          {/* ROOT CAUSE AND IMPACT */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

            <div className="rounded-lg bg-[#071226] p-4">
              <h4 className="text-sm font-semibold text-orange-400 mb-2">
                Root Cause
              </h4>

              <p className="text-sm text-slate-300 leading-6">
                {insight.rootCause || 'Not identified'}
              </p>
            </div>

            <div className="rounded-lg bg-[#071226] p-4">
              <h4 className="text-sm font-semibold text-yellow-400 mb-2">
                Impact
              </h4>

              <p className="text-sm text-slate-300 leading-6">
                {insight.impact || 'Not specified'}
              </p>
            </div>

          </div>

          {/* RECOMMENDATION */}
          <div className="rounded-lg border border-green-500/20 bg-green-500/5 p-4">
            <h4 className="text-sm font-semibold text-green-400 mb-2">
              Recommendation
            </h4>

            <p className="text-sm text-slate-300 leading-6">
              {insight.recommendation || 'No recommendation available.'}
            </p>
          </div>

          {/* AI CONFIDENCE */}
          <div className="flex items-center justify-between text-sm">
            <span className="text-slate-400">
              AI Confidence
            </span>

            <span className="font-semibold text-blue-400">
              {insight.confidence ?? 0}%
            </span>
          </div>

        </article>

      ))}

    </div>

  )}

  {/* DELETE CONFIRMATION POPUP */}
  {selectedInsight && (

    <div className="fixed inset-0 z-50 flex items-center justify-center
                    bg-black/70 backdrop-blur-sm p-4">

      <div className="w-full max-w-md rounded-2xl border border-slate-700
                      bg-[#0b172c] p-6 shadow-2xl">

        {/* POPUP HEADER */}
        <div className="flex items-center justify-between mb-4">

          <h3 className="text-lg font-semibold text-white">
            Delete AI Insight?
          </h3>

          <button
            type="button"
            disabled={deletingInsight}
            onClick={() => {
              setSelectedInsight(null)
              setDeleteError('')
            }}
            className="text-slate-400 hover:text-white text-2xl
                       disabled:opacity-50"
          >
            ×
          </button>

        </div>

        {/* POPUP MESSAGE */}
        <p className="text-sm text-slate-400 leading-6">
          Are you sure you want to delete this AI insight?
          This action cannot be undone.
        </p>

        {/* ERROR MESSAGE */}
        {deleteError && (
          <p className="text-sm text-red-400 mt-3">
            {deleteError}
          </p>
        )}

        {/* POPUP BUTTONS */}
        <div className="flex justify-end gap-3 mt-6">

          <button
            type="button"
            disabled={deletingInsight}
            onClick={() => {
              setSelectedInsight(null)
              setDeleteError('')
            }}
            className="px-4 py-2 rounded-lg border border-slate-700
                       text-slate-300 hover:bg-slate-800
                       disabled:opacity-50"
          >
            Cancel
          </button>

          <button
            type="button"
            disabled={deletingInsight}
            onClick={handleDeleteInsight}
            className="px-4 py-2 rounded-lg bg-red-600
                       text-white hover:bg-red-700
                       disabled:opacity-50"
          >
            {deletingInsight ? 'Deleting...' : 'Delete'}
          </button>

        </div>

      </div>

    </div>

  )}

</section>

    </div>
  )
}
