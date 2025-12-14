import { UseFormReturn } from "react-hook-form"
import InputFormField from "@/components/custom/form-field/input"
import SwitchFormField from "@/components/custom/form-field/switch"
import SelectFormField from "@/components/custom/form-field/select"
import ComboboxFormField from "@/components/custom/form-field/combobox"
import PhoneInputFormField from "@/components/custom/form-field/phone-input"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Separator } from "@/components/ui/separator"
import Notice from "@/components/custom/display/notice"
import { Skeleton } from "@/components/ui/skeleton"
import { locales } from "@/data/locales"
import { Option } from "@/components/custom/form-field/multi-selector"
import { type User } from "@/domains/user"
import TeamRoleSelectorFormField from "@/components/custom/form-field/team-role-selector"

interface UserFormFieldsProps {
  form: UseFormReturn<User>
  id?: string | null
  loginUsername?: string | null
  enabledRoles: string[]
  supportedTimezones: Option[]
  supportedDateTimeFormats: Option[]
}

export function UserFormFields({
  form,
  id,
  loginUsername,
  enabledRoles,
  supportedTimezones,
  supportedDateTimeFormats
}: UserFormFieldsProps) {
  return (
    <>
      {loginUsername === id && <Notice message="After modifying the current logged-in user information, you will be automatically logged out." />}
      <div className="flex flex-col md:flex-row items-center justify-between gap-5">
        <div className="flex items-center gap-2">
          <div className="relative">
            <Avatar className="size-20">
              <AvatarImage src={"/api/user-avatars/" + id} alt="avatar" />
              <AvatarFallback><Skeleton className="w-full" /></AvatarFallback>
            </Avatar>
            {loginUsername === id && <span className="animate-pulse border-background absolute -end-0.5 -bottom-0.5 size-6 rounded-full border-4 bg-emerald-500">
              <span className="sr-only">Online</span>
            </span>}
          </div>
        </div>

        <Separator orientation="vertical" className="hidden md:block" />
        <InputFormField
          control={form.control}
          name="username"
          label="Username"
          formItemClassName="w-full"
          required
          disabled={!!id}
        />
      </div>

      <InputFormField
        control={form.control}
        name="email"
        label="Email"
        required
      />

      <div className="flex flex-col md:flex-row items-center gap-2">
        <InputFormField
          control={form.control}
          name="firstName"
          label="First Name"
          formItemClassName="w-full"
        />

        <InputFormField
          control={form.control}
          name="lastName"
          label="Last Name"
          formItemClassName="w-full"
        />
      </div>

      <div className="flex flex-col md:flex-row items-center gap-2">
        <PhoneInputFormField
          control={form.control}
          name="mobileNo"
          label="Mobile No"
          formItemClassName="w-full"
          required
          placeholder="Enter a phone number"
        />

        <SelectFormField
          control={form.control}
          name="locale"
          label="Preferred Language"
          options={locales}
          formItemClassName="w-full"
          required
        />
      </div>

      <div className="flex flex-col md:flex-row items-center gap-2">
        <ComboboxFormField
          control={form.control}
          name="timeZoneId"
          label="Time Zone"
          options={supportedTimezones}
          formItemClassName="w-full"
          required
        />

        <SelectFormField
          control={form.control}
          name="dateTimeFormatId"
          label="Date Time Format"
          options={supportedDateTimeFormats}
          formItemClassName="w-full"
          required
        />
      </div>

      <InputFormField
        control={form.control}
        name="remark"
        label="Remark"
      />

      <TeamRoleSelectorFormField
        control={form.control}
        name="roleIds"
        label="Roles"
        required
        allRoles={enabledRoles}
      />

      <SwitchFormField
        control={form.control}
        name="enabled"
        label="Enabled"
        description="Prevent login when disabled."
      />
    </>
  )
}
