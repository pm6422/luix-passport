import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { toast } from "sonner"
import { LoadingButton } from "@/components/custom/loading-button"
import { getErrorMessage } from "@/lib/handle-error"
import { ContactService } from "@/services/contact-service"
import InputFormField from '@/components/custom/form-field/input'
import {
  Form,
} from "@/components/ui/form";

// Define the form schema with updated field names
const contactFormSchema = z.object({
  sender: z.string().trim().min(1, { message: "Required" }),
  senderEmail: z.string().email("Please enter a valid email address"),
  title: z.string().trim().min(1, { message: "Required" }),
  content: z.string().trim().min(1, { message: "Required" })
})

export type ContactFormSchema = z.infer<typeof contactFormSchema>

export default function ContactUs() {
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<ContactFormSchema>({
    resolver: zodResolver(contactFormSchema),
    defaultValues: {
      sender: "",
      senderEmail: "",
      title: "",
      content: ""
    }
  })

  async function onSubmit(data: ContactFormSchema) {
    setIsLoading(true)

    toast.promise(ContactService.create(data), {
      loading: "Sending message...",
      success: () => {
        setIsLoading(false)
        window.location.href = "/login";
        return "Sent message successfully"
      },
      error: (error) => {
        setIsLoading(false)
        return getErrorMessage(error)
      }
    })
  }

  return (
    <div className="container relative grid flex-col items-center justify-center lg:max-w-none lg:grid-cols-2">
      {/* Left side - hidden on mobile */}
      <div className="lg:flex relative hidden lg:px-28 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px]">
            We're Here to Help
          </h2>
          <h4 className="mt-6 text-lg">
            Have questions or feedback? Reach out to our team anytime.
          </h4>
          <p className="text-sm mt-10">
            Typically respond within 24 hours
          </p>
        </div>
      </div>

      {/* Right side - form */}
      <div className="flex flex-col w-full xl:px-48 lg:px-28 px-5 rounded-3xl py-10">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="h-16 my-5 mx-auto"
        />

        <div className="w-full max-w-md mx-auto">
          <h1 className="text-2xl font-bold mb-2 text-center">Contact Us</h1>
          <p className="text-sm text-muted-foreground mb-8 text-center">
            Fill out the form below and we'll get back to you soon
          </p>

          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              <InputFormField
                control={form.control}
                name="sender"
                label="Full Name"
                formItemClassName="text-left"
                inputClassName="w-full h-14 px-5 py-4 rounded-2xl"
                placeholder="Your name"
                required={true}
              />

              <InputFormField
                control={form.control}
                name="senderEmail"
                label="Email Address"
                formItemClassName="text-left"
                inputClassName="w-full h-14 px-5 py-4 rounded-2xl"
                placeholder="your@email.com"
                required={true}
              />

              <div>
                <label htmlFor="title" className="block text-sm font-medium mb-2">
                  Subject
                </label>
                <select
                  id="title"
                  className="w-full px-5 py-4 h-14 text-base rounded-2xl border border-input bg-background"
                  {...form.register("title")}
                >
                  <option value="">Select a subject</option>
                  <option value="support">Technical Support</option>
                  <option value="billing">Billing Inquiry</option>
                  <option value="feedback">Product Feedback</option>
                  <option value="other">Other</option>
                </select>
                {form.formState.errors.title && (
                  <p className="mt-1 text-sm text-destructive">
                    {form.formState.errors.title.message}
                  </p>
                )}
              </div>

              <InputFormField
                control={form.control}
                name="content"
                label="Message"
                type="textarea"
                formItemClassName="text-left"
                inputClassName="w-full h-40 px-5 py-4 rounded-2xl"
                placeholder="How can we help you?"
                required={true}
              />

              <LoadingButton
                type="submit"
                loading={isLoading}
                className="w-full px-5 py-4 h-14 mt-4 text-base font-medium rounded-2xl bg-primary text-primary-foreground hover:bg-primary/90"
              >
                {isLoading ? "Sending..." : "Send Message"}
              </LoadingButton>
            </form>
          </Form>

          <div className="mt-8 pt-8 border-t">
            <h3 className="text-lg font-semibold mb-4">Other Ways to Reach Us</h3>
            <div className="space-y-3">
              <div className="flex items-center">
                <svg className="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                <span>helpdesk@luixtech.cn</span>
              </div>
              <div className="flex items-center">
                <svg className="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                </svg>
                <span>+1 (555) 123-4567</span>
              </div>
              <div className="flex items-center">
                <svg className="h-5 w-5 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                <span>123 Business Ave, Suite 100<br />San Francisco, CA 94107</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}