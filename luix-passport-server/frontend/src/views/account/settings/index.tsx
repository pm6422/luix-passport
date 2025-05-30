import { Separator } from "@/components/ui/separator"
import { SettingsForm } from "./settings-form.tsx"

export default function Settings() {
  return (
    <div className="space-y-6">
      <div>
        <h3 className="text-lg font-medium">Account Settings</h3>
        <p className="text-sm text-muted-foreground">
          Update your account settings. Set your preferred language and timezone.
        </p>
      </div>
      <Separator />
      <SettingsForm />
    </div>
  )
}
