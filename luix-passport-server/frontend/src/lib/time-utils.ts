import dayjs from 'dayjs'

export function formatDateTime(
  dateTimeFormat: string,
  timeZone: string,
  value?: string | null,
): string {
  if (!value) {
    return ''
  }
  return dayjs(value).tz(timeZone).format(dateTimeFormat)
}

export function formatDate(
  dateFormat: string,
  timeZone: string,
  value?: string | null,
): string {
  if (!value) {
    return ''
  }
  return dayjs(value).tz(timeZone).format(dateFormat)
}
