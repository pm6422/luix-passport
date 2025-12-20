import { UseFormReturn } from "react-hook-form"
import { GeneratePromoCode } from "@/domains/promo-code"
import InputFormField from "@/components/custom/form-field/input"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { MembershipBadge } from "@/components/custom/display/membership-badge"
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"

interface GeneratePromoCodeFormFieldsProps {
  form: UseFormReturn<GeneratePromoCode>
  roleOptions: { label: string, value: string }[]
}

export function GeneratePromoCodeFormFields({
  form,
  roleOptions
}: GeneratePromoCodeFormFieldsProps) {
  return (
    <div className="grid gap-4 py-4">
      <InputFormField
        control={form.control}
        name="count"
        label="Count"
        placeholder="Number of codes to generate"
        type="number"
      />
      <FormField
        control={form.control}
        name="type"
        render={({ field }) => (
          <FormItem className="space-y-3">
            <FormLabel>Membership Type</FormLabel>
            <FormControl>
              <RadioGroup
                onValueChange={field.onChange}
                defaultValue={field.value}
                className="flex flex-row space-x-4 space-y-0"
              >
                {roleOptions.map((option) => (
                  <FormItem key={option.value} className="flex items-center space-x-3 space-y-0">
                    <FormControl>
                      <RadioGroupItem value={option.value} />
                    </FormControl>
                    <FormLabel className="font-normal cursor-pointer">
                      <MembershipBadge type={option.value} className="h-6 px-2" />
                    </FormLabel>
                  </FormItem>
                ))}
              </RadioGroup>
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />
      <InputFormField
        control={form.control}
        name="durationDays"
        label="Duration (Days)"
        placeholder="Duration in days"
        type="number"
      />
      <InputFormField
        control={form.control}
        name="batchId"
        label="Batch ID (Optional)"
        placeholder="Custom batch ID"
      />
      <InputFormField
        control={form.control}
        name="expiry"
        label="Expiry Date (Optional)"
        placeholder="YYYY-MM-DD"
        type="date"
      />
    </div>
  )
}
