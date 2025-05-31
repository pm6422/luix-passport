import { Link } from "react-router-dom"

export default function PrivacyPolicy() {
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
            Your Privacy Matters
          </h2>
          <h4 className="mt-6 text-lg">
            We're committed to protecting your personal information.
          </h4>
          <p className="text-sm mt-10">
            Questions?{" "}
            <Link to="/contact-us" className="font-bold text-blue-600 hover:underline">
              Contact us
            </Link>
          </p>
        </div>
      </div>

      {/* Right side - content */}
      <div className="flex flex-col w-full h-full xl:px-48 lg:px-28 px-5 text-left rounded-3xl overflow-y-auto py-10">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="h-16 my-5 mx-auto"
        />

        <div className="prose max-w-none">
          <h1 className="text-2xl font-bold mb-6 text-center">Privacy Policy</h1>
          <p className="text-sm text-muted-foreground mb-8 text-center">Last Updated: January 1, 2023</p>

          <section className="mb-8">
            <h2 className="text-xl font-semibold mb-4">1. Information We Collect</h2>
            <p className="mb-4">
              We collect information you provide directly, such as when you create an account.
            </p>
          </section>

          <section className="mb-8">
            <h2 className="text-xl font-semibold mb-4">2. How We Use Information</h2>
            <p className="mb-4">
              We use the information to provide, maintain, and improve our services.
            </p>
          </section>

          <section className="mb-8">
            <h2 className="text-xl font-semibold mb-4">3. Information Sharing</h2>
            <p className="mb-4">
              We do not share your personal information with third parties except as described in this policy.
            </p>
          </section>

          <section className="mb-8">
            <h2 className="text-xl font-semibold mb-4">4. Security</h2>
            <p className="mb-4">
              We implement reasonable security measures to protect your information.
            </p>
          </section>

          <section className="mb-8">
            <h2 className="text-xl font-semibold mb-4">5. Changes to This Policy</h2>
            <p className="mb-4">
              We may update this policy from time to time. We will notify you of any changes.
            </p>
          </section>

          <p className="text-sm text-muted-foreground mt-10">
            By using our services, you agree to the collection and use of information in accordance with this policy.
          </p>
        </div>
      </div>
    </div>
  );
}