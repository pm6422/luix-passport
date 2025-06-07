import { useState, useEffect } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { Button } from "@/components/ui/button"
import { LoadingButton } from "@/components/custom/loading-button"
import { Form } from "@/components/ui/form"
import InputFormField from "@/components/custom/form-field/input"
import SelectFormField from "@/components/custom/form-field/select"
import ComboboxFormField from "@/components/custom/form-field/combobox"
import { IconMailForward } from "@tabler/icons-react"
import { locales } from "@/data/locales"
import { toast } from "sonner"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { AccountService } from "@/services/account-service"
import { getErrorMessage } from "@/lib/handle-error"
import { isValidPhoneNumber } from "react-phone-number-input"
import { type SupportedDateTimeFormat } from "@/domains/supported-date-time-format"
import { type DataDict } from "@/domains/data-dict"
import { Link } from "react-router-dom"
import { Option } from "@/components/custom/multi-select"

const settingsFormSchema = z.object({
  username: z.string().trim().min(1, { message: "Required" }),
  email: z.string().trim().min(1, { message: "Required" }).email("Invalid email format"),
  mobileNo: z.string().trim().min(1, { message: "Required" }).refine(isValidPhoneNumber, { message: "Invalid phone number" }),
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  locale: z.string().trim().min(1, { message: "Required" }),
  timeZoneId: z.string().trim().min(1, { message: "Required" }),
  dateTimeFormatId: z.string().trim().min(1, { message: "Required" }),
})

export type SettingsFormSchema = z.infer<typeof settingsFormSchema>

export function SettingsForm() {
  const { loginUser } = useStore(loginUserStore)
  const [saving, setSaving] = useState(false)
  const [supportedTimezones, setSupportedTimezones] = useState(Array<Option>)
  const [supportedDateTimeFormats, setSupportedDateTimeFormats] = useState(Array<Option>)

  const form = useForm<SettingsFormSchema>({
    resolver: zodResolver(settingsFormSchema),
    defaultValues: loginUser
  })

  useEffect(() => {
    Promise.all([
      AccountService.findSupportedTimezones(),
      AccountService.findSupportedDateTimeFormats()
    ]).then(results => {
      // load options
      setSupportedTimezones(results[0].data.map((item: DataDict) =>
        ({label: "(UTC" + item.dictName + ") " + item.dictCode , value: item.dictCode})));
      setSupportedDateTimeFormats(results[1].data.map((item: SupportedDateTimeFormat) =>
        ({label: item.displayName + " (" + item.example + ")", value: item.id})));

      AccountService.getCurrentUser().then(user => {
        form.reset(user)
      })
    })
  }, [form])

  function onSubmit(formData: SettingsFormSchema) {
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
          <p className="mt-2 text-sm text-muted-foreground">
            {form.getValues("username")}
          </p>
        </div>

        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-sm font-medium">Email</h3>
            <p className="mt-2 text-sm text-muted-foreground">
              {form.getValues("email")}
            </p>
          </div>
          <Link to="/account/change-email">
            <Button
              type="button"
              variant={"link"}
              className="underline decoration-dashed underline-offset-4 hover:decoration-solid"
            >
              <IconMailForward className="mr-2 size-4" />
              Change Email
            </Button>
          </Link>
        </div>

        <div>
          <h3 className="text-sm font-medium">Mobile No.</h3>
          <p className="mt-2 text-sm text-muted-foreground">
            {form.getValues("mobileNo")}
          </p>
        </div>

        <InputFormField
          control={form.control}
          name="firstName"
          label="First Name"
        />
        <InputFormField
          control={form.control}
          name="lastName"
          label="Last Name"
        />
        <SelectFormField
          control={form.control}
          name="locale"
          label="Preferred Language"
          options={locales}
          required
        />
        <ComboboxFormField
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

        <div className="flex justify-end">
          <LoadingButton type="submit" loading={saving} className="w-full sm:w-auto">
            {saving ? "Updating account" : "Update account"}
          </LoadingButton>
        </div>
      </form>
    </Form>
  )
}
