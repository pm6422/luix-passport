export default function ContactUs() {
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

          <form className="space-y-6">
            <div>
              <label htmlFor="name" className="block text-sm font-medium mb-2">
                Full Name
              </label>
              <input
                type="text"
                id="name"
                className="w-full px-5 py-4 h-14 text-base rounded-2xl border border-input bg-background"
                placeholder="Your name"
                required
              />
            </div>

            <div>
              <label htmlFor="email" className="block text-sm font-medium mb-2">
                Email Address
              </label>
              <input
                type="email"
                id="email"
                className="w-full px-5 py-4 h-14 text-base rounded-2xl border border-input bg-background"
                placeholder="your@email.com"
                required
              />
            </div>

            <div>
              <label htmlFor="subject" className="block text-sm font-medium mb-2">
                Subject
              </label>
              <select
                id="subject"
                className="w-full px-5 py-4 h-14 text-base rounded-2xl border border-input bg-background"
                required
              >
                <option value="">Select a subject</option>
                <option value="support">Technical Support</option>
                <option value="billing">Billing Inquiry</option>
                <option value="feedback">Product Feedback</option>
                <option value="other">Other</option>
              </select>
            </div>

            <div>
              <label htmlFor="message" className="block text-sm font-medium mb-2">
                Message
              </label>
              <textarea
                id="message"
                rows={4}
                className="w-full px-5 py-4 text-base rounded-2xl border border-input bg-background"
                placeholder="How can we help you?"
                required
              ></textarea>
            </div>

            <button
              type="submit"
              className="w-full px-5 py-4 h-14 mt-4 text-base font-medium rounded-2xl bg-primary text-primary-foreground hover:bg-primary/90"
            >
              Send Message
            </button>
          </form>

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
  );
}