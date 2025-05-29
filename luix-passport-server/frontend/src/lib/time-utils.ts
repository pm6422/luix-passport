import dayjs from 'dayjs'

export function formatDateTime(
  dateTimeFormat: string,
  value: string | Date,
  timeZone: string
): string {
  if (!value) {
    return ''
  }
  return dayjs(value).tz(timeZone).format(dateTimeFormat)
}

export function formatDate(
  dateFormat: string,
  value: string | Date,
  timeZone: string
): string {
  if (!value) {
    return ''
  }
  return dayjs(value).tz(timeZone).format(dateFormat)
}
