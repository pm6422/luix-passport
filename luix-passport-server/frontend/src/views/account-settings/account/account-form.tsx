import { useState, useEffect } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { Button } from "@/components/custom/button"
import { Form } from "@/components/ui/form"
import InputFormField from "@/components/custom/form-field/input"
import SelectFormField from "@/components/custom/form-field/select"
import { IconReload, IconMailForward } from "@tabler/icons-react"
import { locales } from "@/data/locales"
import { toast } from "sonner"
import { useStore } from "exome/react"
import { authUserStore } from "@/stores/auth-user-store.ts"
import { AccountService } from "@/services/account-service"
import { UserService } from "@/services/user-service"
import { getErrorMessage } from "@/lib/handle-error"
import { isValidPhoneNumber } from "react-phone-number-input"
import { type Option } from "@/components/custom/form-field/combo-box"
import { type SupportedTimezone } from "@/domains/supported-timezone"
import { type SupportedDateTimeFormat } from "@/domains/supported-date-time-format"
import { Link } from "react-router-dom"

const formSchema = z.object({
  id: z.string().trim().min(1, { message: "Required" }),
  username: z.string().trim().min(1, { message: "Required" }),
  email: z.string().trim().min(1, { message: "Required" }).email("Invalid email format"),
  mobileNo: z.string().trim().min(1, { message: "Required" }).refine(isValidPhoneNumber, { message: "Invalid phone number" }),
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  locale: z.string().trim().min(1, { message: "Required" }),
  timeZoneId: z.string().trim().min(1, { message: "Required" }),
  dateTimeFormatId: z.string().trim().min(1, { message: "Required" }),
})

type FormSchema = z.infer<typeof formSchema>

export function AccountForm() {
  const { authUser } = useStore(authUserStore)
  const [saving, setSaving] = useState(false)
  const [supportedTimezones, setSupportedTimezones] = useState(Array<Option>)
  const [supportedDateTimeFormats, setSupportedDateTimeFormats] = useState(Array<Option>)

  const form = useForm<FormSchema>({
    resolver: zodResolver(formSchema),
    defaultValues: authUser
  })

  useEffect(() => {
    AccountService.getCurrentAccount().then(user => {
      form.reset(user)
    })

    UserService.findSupportedTimezones().then(r => {
      setSupportedTimezones(r.data.map((item: SupportedTimezone) =>
        ({label: item.displayName + " (UTC" + item.utcOffset + ")", value: item.id})))
    })

    UserService.findSupportedDateTimeFormats().then(r => {
      setSupportedDateTimeFormats(r.data.map((item: SupportedDateTimeFormat) =>
        ({label: item.displayName + " (" + item.example + ")", value: item.id})))
    })
  }, [])

  function onSubmit(formData: FormSchema) {
    setSaving(true)
    toast.promise(AccountService.update(formData), {
      loading: "Updating account...",
      success: () => {
        setSaving(false)
        return "Updated account"
      },
      error: (error) => {
        setSaving(false)
        return getErrorMessage(error)
      }
    })
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
        <div>
          <h3 className="text-sm font-medium">Username</h3>
          <p className="text-sm text-muted-foreground mt-2">
            {form.getValues("username")}
          </p>
        </div>

        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-sm font-medium">Email</h3>
            <p className="text-sm text-muted-foreground mt-2">
              {form.getValues("email")}
            </p>
          </div>
          <Link to="/account-settings/change-email">
            <Button type="button" variant={"link"} className="underline decoration-dashed underline-offset-4 hover:decoration-solid">
              <IconMailForward className="mr-2 size-4"/>
              Change Email
            </Button>
          </Link>
        </div>

        <div>
          <h3 className="text-sm font-medium">Mobile No.</h3>
          <p className="text-sm text-muted-foreground mt-2">
            {form.getValues("mobileNo")}
          </p>
        </div>

        <InputFormField control={form.control} name="firstName" label="First Name"/>
        <InputFormField control={form.control} name="lastName" label="Last Name"/>
        <SelectFormField 
          control={form.control} 
          name="locale" 
          label="Preferred Language"
          options={locales}
          required
        />
        <SelectFormField 
          control={form.control} 
          name="timeZoneId"
          label="Time Zone"
          options={supportedTimezones}
          required
        />
        <SelectFormField
          control={form.control} 
          name="dateTimeFormatId"
          label="Date Time Format"
          options={supportedDateTimeFormats}
          required
        />

        <Button type="submit" disabled={saving}>
          {saving ? "Updating account..." : "Update account"}
          {saving && (<IconReload className="ml-1 size-4 animate-spin"/>)}
        </Button>
      </form>
    </Form>
  )
}
