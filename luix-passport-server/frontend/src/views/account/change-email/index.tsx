import { ChangeEmailForm } from './change-email-form'
import { Separator } from '@/components/ui/separator'

export default function SettingsAppearance() {
  return (
    <div className='space-y-6'>
      <div>
        <h3 className='text-lg font-medium'>Change Email</h3>
        <p className='text-sm text-muted-foreground'>
          Change your email of the application.
        </p>
      </div>
      <Separator />
      <ChangeEmailForm />
    </div>
  )
}
