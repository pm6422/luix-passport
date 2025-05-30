import { Separator } from "@/components/ui/separator"
import ProfileForm from "./profile-form"

export default function SettingsProfile() {
  return (
    <div className="space-y-6">
      <div>
        <h3 className="text-lg font-medium">Profile Picture</h3>
        <p className="text-sm text-muted-foreground">
          A picture helps people recognize you and lets you know when you’re signed in to your account.
        </p>
      </div>
      <Separator />
      <ProfileForm />
    </div>
  )
}
