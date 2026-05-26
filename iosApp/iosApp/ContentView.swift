import SwiftUI
// import shared

struct ContentView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "arrow.left.arrow.right.circle.fill")
                .font(.system(size: 64))
                .foregroundStyle(.tint)
            Text("Currency Converter")
                .font(.largeTitle)
                .fontWeight(.bold)
            Text("iOS UI coming soon")
                .foregroundStyle(.secondary)
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
