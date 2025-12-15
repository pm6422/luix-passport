import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'

dayjs.extend(utc)
dayjs.extend(timezone)

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

export function formatTime(
  timeFormat: string,
  timeZone: string,
  value?: string | null,
): string {
  if (!value) {
    return ''
  }
  return dayjs(value).tz(timeZone).format(timeFormat)
}
