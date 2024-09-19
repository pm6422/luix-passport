export type DateTimeFormat = {
  value: string
  label: string
  dateTimeFormat: string
  dateFormat: string
  timeFormat: string
}

export const dateTimeFormats = [
  { value: "1", label: "2021-09-15 10:45:00", dateTimeFormat: "YYYY-MM-DD HH:mm:ss", dateFormat: "YYYY-MM-DD", timeFormat: "HH:mm:ss" },
  { value: "2", label: "15 Sep 2021, 10:45 AM", dateTimeFormat: "DD MMM YYYY, HH:mm A", dateFormat: "DD MMM YYYY", timeFormat: "HH:mm A" },
  { value: "3", label: "15/09/2021, 10:45 AM", dateTimeFormat: "DD/MM/YYYY, HH:mm A", dateFormat: "DD/MM/YYYY", timeFormat: "HH:mm A" },
  { value: "4", label: "Sep 15, 2021 10:45 am", dateTimeFormat: "MMM DD, YYYY HH:mm a", dateFormat: "DD/MM/YYYY", timeFormat: "HH:mm a" }
]

