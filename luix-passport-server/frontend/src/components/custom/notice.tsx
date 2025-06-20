import { IconExclamationCircle } from '@tabler/icons-react'
import { Alert, AlertTitle } from '@/components/ui/alert'

interface Props {
  message: string
}

const Notice = ({ message }: Props) => {
  return (
    <Alert className='bg-yellow-600/10 dark:bg-yellow-600/20 hover:bg-yellow-600/10 text-yellow-500 shadow-none'>
      <AlertTitle className='flex items-center'>
        <IconExclamationCircle className='me-1 size-5' />
          {message}
      </AlertTitle>
    </Alert>
  )
}

export default Notice
